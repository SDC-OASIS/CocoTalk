//
//  AppDelegate.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/06.
//

import UIKit
import CoreData
import Firebase
import FirebaseMessaging
import IQKeyboardManagerSwift
import SwiftKeychainWrapper
import UserNotifications

@main
class AppDelegate: UIResponder, UIApplicationDelegate {
    
    var listSocket: WebSocketHelper?
    var chatSocket: WebSocketHelper?
    var socketDelegate: SceneDelegate?
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        
        IQKeyboardManager.shared.enable = true
        IQKeyboardManager.shared.enableAutoToolbar = false
        IQKeyboardManager.shared.shouldResignOnTouchOutside = true
        
        FirebaseApp.configure()
        Messaging.messaging().delegate = self
        
        UNUserNotificationCenter.current().delegate = self
        
        let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
        UNUserNotificationCenter.current().requestAuthorization(options: authOptions) { granted, error in
            if granted {
                print("알림 등록이 완료되었습니다.")
            }
        }
        application.registerForRemoteNotifications()
        
        return true
    }

    // MARK: UISceneSession Lifecycle

    func application(_ application: UIApplication, configurationForConnecting connectingSceneSession: UISceneSession, options: UIScene.ConnectionOptions) -> UISceneConfiguration {
        return UISceneConfiguration(name: "Default Configuration", sessionRole: connectingSceneSession.role)
    }

    func application(_ application: UIApplication, didDiscardSceneSessions sceneSessions: Set<UISceneSession>) {
    }
    
    func application(_ application: UIApplication, supportedInterfaceOrientationsFor window: UIWindow?) -> UIInterfaceOrientationMask {
        return UIDevice.current.userInterfaceIdiom == .phone ? .portrait : .all
    }

    // MARK: - Core Data stack

    lazy var persistentContainer: NSPersistentContainer = {
        let container = NSPersistentContainer(name: "CocoTalk")
        container.loadPersistentStores(completionHandler: { (storeDescription, error) in
            if let error = error as NSError? {
                fatalError("Unresolved error \(error), \(error.userInfo)")
            }
        })
        return container
    }()

    // MARK: - Core Data Saving support

    func saveContext () {
        let context = persistentContainer.viewContext
        if context.hasChanges {
            do {
                try context.save()
            } catch {
                let nserror = error as NSError
                fatalError("Unresolved error \(nserror), \(nserror.userInfo)")
            }
        }
    }

}

// MARK: - UNUserNotificationCenterDelegate
extension AppDelegate: UNUserNotificationCenterDelegate {
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        Messaging.messaging().apnsToken = deviceToken
    }
}

// MARK: - MessagingDelegate
extension AppDelegate: MessagingDelegate {
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        #warning("이전 코드랑 다르면 post 호출")
        KeychainWrapper.standard[.fcmToken] = fcmToken ?? ""
    }
}

// MARK: - WebSocket
extension AppDelegate {
    func initializeListSocket() {
        if let savedData = UserDefaults.standard.object(forKey: UserDefaultsKey.myData.rawValue) as? Data,
           let data = try? JSONDecoder().decode(ModelSignupResponse.self, from: savedData) {
            listSocket = WebSocketHelper(socketType: .chatList, userId: data.id)
            listSocket?.establishConnection()
        }
    }
    
    func initializeChatSocket() {
        chatSocket?.establishConnection()
    }
    
    func closeChatSocket() {
        chatSocket?.closeConnection()
    }
    
    func removeChatSocket() {
        chatSocket = nil
    }
    
    func signout() {
        socketDelegate?.signoutBySocket()
    }
}
