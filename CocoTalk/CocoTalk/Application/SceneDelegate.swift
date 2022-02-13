//
//  SceneDelegate.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/06.
//

import UIKit
import RxSwift
import SwiftKeychainWrapper

class SceneDelegate: UIResponder, UIWindowSceneDelegate {

    var window: UIWindow?
    
    let authRepository = AuthRepository()
    let bag = DisposeBag()
    let viewModel = SplashViewModel()


    func scene(_ scene: UIScene, willConnectTo session: UISceneSession, options connectionOptions: UIScene.ConnectionOptions) {
        guard let windowScene = (scene as? UIWindowScene) else { return }
        window = UIWindow(frame: UIScreen.main.bounds)
        window?.windowScene = windowScene
        
        bindRx()
        
        let vc = SplashViewController()
        let nav = UINavigationController(rootViewController: vc)
        
        window?.backgroundColor = .white
        window?.rootViewController = nav
        window?.makeKeyAndVisible()
    }

    func sceneDidDisconnect(_ scene: UIScene) {
    }

    func sceneDidBecomeActive(_ scene: UIScene) {
    }

    func sceneWillResignActive(_ scene: UIScene) {
    }

    func sceneWillEnterForeground(_ scene: UIScene) {
        viewModel.verifyToken()
    }

    func sceneDidEnterBackground(_ scene: UIScene) {
        let appDelegate = UIApplication.shared.delegate as? AppDelegate
        appDelegate?.listSocket?.closeConnection()
        appDelegate?.saveContext()
    }


}

extension SceneDelegate {
    func bindRx() {
        viewModel.dependency.shouldSignout
            .subscribe(onNext: { [weak self] shouldSignout in
                guard let self = self,
                      let shouldSignout = shouldSignout else {
                    return
                }
                
                if shouldSignout {
                    UserDefaults.resetUserData()
                    KeychainWrapper.resetKeys()
                    let vc = SigninViewController()
                    let nav = UINavigationController(rootViewController: vc)
                    
                    self.window?.backgroundColor = .white
                    self.window?.rootViewController = nav
                    self.window?.makeKeyAndVisible()
                } else {
                    let appDelegate = UIApplication.shared.delegate as? AppDelegate
                    appDelegate?.initializeListSocket()
                }
            }).disposed(by: bag)
    }
}
