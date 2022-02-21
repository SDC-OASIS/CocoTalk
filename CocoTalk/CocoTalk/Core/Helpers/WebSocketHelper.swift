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

/// 웹소켓 연결을 지원하는 클래스
final class WebSocketHelper: StompClientLibDelegate {
     
    
    // MARK: - Private properties
    private var chatRoomRepository = ChatRoomRepository()
    private var userId: String = ""
    private var roomId: String = ""
    private var socketType: SocketType
    private var destinationToType: [String: SocketTopicEnum] = [:]
    private var bag = DisposeBag()
    
    
    // MARK: - Public properties
    var socketClient: StompClientLib?
    let header: [String: String]
    
    /// 받은 메시지
    var receivedMessage = BehaviorRelay<ModelMessageVO?>(value: nil)
    
    /// 업데이트된 방 정보
    var receivedUpdatedRoomInfo = BehaviorRelay<ModelRoom?>(value: nil)
    
    /// 새로 생성된 방
    var receivedNewRoom = BehaviorRelay<ModelRoom?>(value: nil)
    
    /// 방 생성 요청 로그
    var createChatRequestLog = BehaviorRelay<ModelCreateChatRoomRequest?>(value: nil)
    
    /// 커넥션 성공
    var isSocketConnected = BehaviorRelay<Bool?>(value: nil)
    
    /// 채팅 관리 서버 요청 결과
    private var assignedChatServerDestination = BehaviorRelay<String?>(value: nil)
    
    
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
        bind()
    }
    
    /// 소켓 연결
    func establishConnection() {
        requestDestination()
    }
    
    func openSocketWithURL(destination: String) {
        let url = URL(string: "\(destination)/websocket")
        socketClient = StompClientLib()
        socketClient?.openSocketWithURLRequest(request: NSURLRequest(url: url! as URL) ,
                                               delegate: self,
                                               connectionHeaders: header)
    }

    /// 소켓 연결 끊기
    func closeConnection() {
        socketClient?.disconnect()
    }
    
    /// 받은 소켓 메시지 핸들링
    func stompClient(client: StompClientLib!, didReceiveMessageWithJSONBody jsonBody: AnyObject?, akaStringBody stringBody: String?, withHeader header: [String : String]?, withDestination destination: String) {
        
        let body = stringBody ?? ""
        
        switch destinationToType[destination] {
        case .some(.listRoomInfo):
            break
        case .some(.listNewRoom):
            receiveNewRoom(body)
            break
        case .some(.listCrash):
            signout(fcmToken: body)
            break
        case .some(.chatRoomInfo):
            receiveUpdateRoomInfo(body)
            break
        case .some(.chatMessage), .some(.listMessage):
            receiveMessage(body)
            break
        case .none:
            break
        }
    }
    
    func stompClientDidDisconnect(client: StompClientLib!) {
        print("\(self.socketType.rawValue) disconnected")
        isSocketConnected.accept(false)
    }
    
    func stompClientDidConnect(client: StompClientLib!) {
        print("\(self.socketType.rawValue) connected")
        isSocketConnected.accept(true)
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
    private func receiveNewRoom(_ newRoomJSONString: String) {
        print("🟢 receive new room\n\(Date())")
        let room = try? JSONDecoder().decode(ModelRoom.self, from: Data(newRoomJSONString.utf8))
        receivedNewRoom.accept(room)
        ChatRoomRepository.newRoom.accept(room)
    }
    
    /// 채팅방 소켓으로 메시지 받기
    private func receiveMessage(_ newMessageJSONString: String) {
        print("🟢 receive message at room\n\(Date()) from CHAT SOCKET")
        let message = try? JSONDecoder().decode(ModelMessageVO.self, from: Data(newMessageJSONString.trimmingCharacters(in: .whitespacesAndNewlines).utf8))
        receivedMessage.accept(message)
    }
    
    /// 업데이트된 채팅방 정보 소켓으로 받기
    private func receiveUpdateRoomInfo(_ updatedRoomInfo: String) {
        print("🟢 receive updated info at room\n\(Date())")
        let info = try? JSONDecoder().decode(ModelRoom.self, from: Data(updatedRoomInfo.trimmingCharacters(in: .whitespacesAndNewlines).utf8))
        receivedUpdatedRoomInfo.accept(info)
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


// MARK: - Request chatting server destination
extension WebSocketHelper {
    func requestDestination() {
        let token: String? = KeychainWrapper.standard[.accessToken]
        guard let token = token else {
            return
        }
        
        chatRoomRepository.getDestination(with: token)
            .subscribe(onNext: { [weak self] response in
                guard let self = self,
                      let destination = response.data else {
                    return
                }
                self.assignedChatServerDestination.accept(destination)
            }).disposed(by: bag)
    }
}


// MARK: - Bind Method
extension WebSocketHelper {
    func bind() {
        bindDestination()
    }
    
    func bindDestination() {
        assignedChatServerDestination
            .subscribe(onNext: { [weak self] destination in
                guard let self = self,
                      let destination = destination else {
                    return
                }
                self.openSocketWithURL(destination: destination)
            }).disposed(by: bag)
    }
}
