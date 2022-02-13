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

final class WebSocketHelper: StompClientLibDelegate {
     
    var socketClient: StompClientLib?
    let header: [String: String]
    
    init(view: SocketType, roomId: String? = "") {
        var userId: Int?
        if let savedData = UserDefaults.standard.object(forKey: UserDefaultsKey.myData.rawValue) as? Data,
           let data = try? JSONDecoder().decode(ModelSignupResponse.self, from: savedData) {
            userId = data.id
        }
        
        if view == .chatList {
            header = ["view": "chatList", "userId": "\(userId ?? -1)"]
        } else {
            header = ["view": "chatRoom", "userId": "\(userId ?? -1)", "roomId": roomId ?? ""]
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
        print(jsonBody ?? "")
        print(stringBody ?? "")
    }
    
    func stompClientDidDisconnect(client: StompClientLib!) {
        print("disconnected")
    }
    
    func stompClientDidConnect(client: StompClientLib!) {
        print("connected")
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
    
    func subscribeTopic(topic: SocketTopicEnum, userId: String?, roomId: String?) {
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
        }
        
        socketClient?.subscribe(destination: destination)
    }
}

enum SocketType {
    case chatList
    case chatRoom
}

