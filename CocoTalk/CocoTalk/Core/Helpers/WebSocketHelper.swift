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
     
    private var userId: String = ""
    private var roomId: String = ""
    private var socketType: SocketType
    
    var socketClient: StompClientLib?
    let header: [String: String]
    private var destinationToType: [String: SocketTopicEnum] = [:]
    
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
    
    func establishConnection() {
        socketClient = StompClientLib()
        socketClient?.openSocketWithURLRequest(request: NSURLRequest(url: .baseSocketURL as URL) ,
                                               delegate: self,
                                               connectionHeaders: header)
    }

    func closeConnection() {
        socketClient?.disconnect()
    }
    
    func stompClient(client: StompClientLib!, didReceiveMessageWithJSONBody jsonBody: AnyObject?, akaStringBody stringBody: String?, withHeader header: [String : String]?, withDestination destination: String) {
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
        print("disconnected")
    }
    
    func stompClientDidConnect(client: StompClientLib!) {
        print("connected")
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
    
    func sendMessage(_ message: ModelChatMessagePub) {
        socketClient?.sendJSONForDict(dict: (message.dictionary ?? [:]) as NSDictionary, toDestination: "/simple/chatroom/\(self.roomId)/message/send")
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
}

// MARK: - SocketType
enum SocketType {
    case chatList
    case chatRoom
}


// MARK: - Subscribe Helpers
extension WebSocketHelper {
    
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

