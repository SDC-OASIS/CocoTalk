//
//  WebSocketHelper.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/09.
//

import Foundation
import RxSwift
import RxRelay
import StompClientLib
import SwiftKeychainWrapper

final class WebSocketHelper: StompClientLibDelegate {
     
    
    // MARK: - Private properties
    private var userId: String = ""
    private var roomId: String = ""
    private var socketType: SocketType
    private var destinationToType: [String: SocketTopicEnum] = [:]
    
    
    // MARK: - Public properties
    var socketClient: StompClientLib?
    let header: [String: String]
    
    /// 새로 생성된 방
    var receviedNewRoom = BehaviorRelay<ModelRoom?>(value: nil)
    
    /// 방 생성 요청 로그
    var createChatRequestLog = BehaviorRelay<ModelCreateChatRoomRequest?>(value: nil)
    
    
    /// 소켓 초기화
    init(socketType: SocketType, userId: Int? = -1, roomId: String? = "") {
        self.socketType = socketType
        self.userId = "\(userId ?? -1)"
        self.roomId = roomId ?? ""
        if socketType == .chatList {
            header = ["view": "chatList", "userId": "\(self.userId)"]
        } else {
            header = ["view": "chatRoom", "userId": "\(self.userId)", "roomId": self.roomId]
        }
    }
    
    /// 소켓 연결
    func establishConnection() {
        socketClient = StompClientLib()
        socketClient?.openSocketWithURLRequest(request: NSURLRequest(url: .baseSocketURL as URL) ,
                                               delegate: self,
                                               connectionHeaders: header)
    }

    /// 소켓 연결 끊기
    func closeConnection() {
        socketClient?.disconnect()
    }
    
    /// 받은 소켓 메시지 핸들링
    func stompClient(client: StompClientLib!, didReceiveMessageWithJSONBody jsonBody: AnyObject?, akaStringBody stringBody: String?, withHeader header: [String : String]?, withDestination destination: String) {
        #warning("삭제")
        print("🟢 STOMP CLIENT MESSAGE 🟢")
        print("[destination]")
        print(destination)
        print("[header]")
        print(header ?? "")
        print("[string body]")
        print(stringBody ?? "")
        
        switch destinationToType[destination] {
        case .some(.chatRoomInfo):
            break
        case .some(.listMessage):
            break
        case .some(.listRoomInfo):
            break
        case .some(.listNewRoom):
            receiveNewRoom(stringBody ?? "")
            break
        case .some(.listCrash):
            signout(fcmToken: stringBody ?? "")
            break
        case .some(.chatMessage):
            break
        case .none:
            break
        }
    }
    
    func stompClientDidDisconnect(client: StompClientLib!) {
        print("\(self.socketType.rawValue) disconnected")
    }
    
    func stompClientDidConnect(client: StompClientLib!) {
        print("\(self.socketType.rawValue) connected")
        if socketType == .chatList {
            subscribeList()
        } else if socketType == .chatRoom {
            subscribeChat()
        }
    }
    
    func serverDidSendReceipt(client: StompClientLib!, withReceiptId receiptId: String) {
        print("Receipt : \(receiptId)")
    }
    
    func serverDidSendError(client: StompClientLib!, withErrorMessage description: String, detailedErrorMessage message: String?) {
        print("Error Send : \(String(describing: message))")
    }
    
    func serverDidSendPing() {
        print("Server ping")
    }
    
    
    // MARK: - Subscribe
    
    private func subscribeList() {
        let userId = self.userId
        subscribeTopic(topic: .listMessage, userId: "\(userId)", roomId: nil)
        subscribeTopic(topic: .listRoomInfo, userId: "\(userId)", roomId: nil)
        subscribeTopic(topic: .listNewRoom, userId: "\(userId)", roomId: nil)
        subscribeTopic(topic: .listCrash, userId: "\(userId)", roomId: nil)
    }
    
    private func subscribeChat() {
        subscribeTopic(topic: .chatMessage, userId: nil, roomId: self.roomId)
        subscribeTopic(topic: .chatRoomInfo, userId: nil, roomId: self.roomId)
    }
    
    private func subscribeTopic(topic: SocketTopicEnum, userId: String?, roomId: String?) {
        let roomId = roomId ?? ""
        let userId = userId ?? ""
        var destination: String
        
        switch topic {
        case .chatMessage:
            destination =  "/topic/\(roomId)/message"
            break
        case .chatRoomInfo:
            destination = "/topic/\(roomId)/room"
            break
        case .listMessage:
            destination = "/topic/\(userId)/message"
            break
        case .listNewRoom:
            destination = "/topic/\(userId)/room/new"
            break
        case .listRoomInfo:
            destination = "/topic/\(userId)/room"
            break
        case .listCrash:
            destination = "/topic/\(userId)/crash/mobile"
        }
        
        print("client subscribed: \(destination)")
        destinationToType[destination] = topic
        socketClient?.subscribe(destination: destination)
    }
    
    
    // MARK: - Helpers
    
    /// 채팅방 생성
    func createRoom(_ roomData: ModelCreateChatRoomRequest) {
        createChatRequestLog.accept(roomData)
        socketClient?.sendJSONForDict(dict: roomData.nsDictionary, toDestination: "/simple/chatroom/new")
    }
    
    /// 메시지 보내기
    func sendMessage(_ message: ModelPubChatMessage) {
        socketClient?.sendJSONForDict(dict: message.nsDictionary, toDestination: "/simple/chatroom/\(message.roomId ?? "")/message/send")
    }
    
    /// 초대 메시지 보내기
    func sendInvite(_ message: ModelPubInvite) {
        socketClient?.sendJSONForDict(dict: message.nsDictionary, toDestination: "/simple/chatroom/\(message.roomId ?? "")/message/invite")
    }
    
}

// MARK: - SocketType
enum SocketType: String {
    case chatList = "리스트 소켓"
    case chatRoom = "채팅방 소켓"
}


// MARK: - Subscribe Helpers
extension WebSocketHelper {
    
    /// 사용자가 포함된 생성된 방에 대한 정보
    private func receiveNewRoom(_ newRoomJsonString: String) {
        print("🟢 receive new room")
        let room = try? JSONDecoder().decode(ModelRoom.self, from: Data(newRoomJsonString.utf8))
        receviedNewRoom.accept(room)
        ChatRoomRepository.newRoom.accept(room)
    }
    
    /// 최근 로그인한 기기가 아니면 로그아웃 시키기
    /// FCM으로 최근 기기인지 아닌지 판단한다.
    private func signout(fcmToken: String) {
        let token: String? = KeychainWrapper.standard[.fcmToken]
        guard let token = token else {
            return
        }
        if token != fcmToken {
            let appDelegate = UIApplication.shared.delegate as? AppDelegate
            appDelegate?.signout()
        }
    }
}

