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
        initializeSocketDelegate()
        
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
        initSockets()
        viewModel.verifyToken()
    }

    func sceneDidEnterBackground(_ scene: UIScene) {
        let appDelegate = UIApplication.shared.delegate as? AppDelegate
        appDelegate?.listSocket?.closeConnection()
        appDelegate?.chatSocket?.closeConnection()
        appDelegate?.saveContext()
    }
    
    // MARK: - Helper
    func initSockets() {
        let appDelegate = UIApplication.shared.delegate as? AppDelegate
        appDelegate?.initListSocket()
        appDelegate?.establishChatSocketConnection()
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
                }
            }).disposed(by: bag)
        
        viewModel.dependency.isValidToken
            .subscribe(onNext: { [weak self] isValid in
                guard let self = self,
                      let isValid = isValid else {
                    return
                }
                
                if !isValid {
                    self.viewModel.reissueToken()
                }
            }).disposed(by: bag)
    }
}

extension SceneDelegate: WebSocketDelegate {
    func initializeSocketDelegate() {
        let appDelegate = UIApplication.shared.delegate as? AppDelegate
        appDelegate?.socketDelegate = self
    }
    
    func signoutBySocket() {
        let alert = UIAlertController(title: "로그아웃", message: "다른 기기에서 로그인되었습니다.", preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "확인", style: .default) { [weak self] _ in
            KeychainWrapper.resetKeys()
            let signInVC = SigninViewController()
            let root = UINavigationController(rootViewController: signInVC)
            self?.window?.backgroundColor = .white
            self?.window?.rootViewController = root
            self?.window?.makeKeyAndVisible()
        })
        window?.rootViewController?.present(alert, animated: true)
    }
}
