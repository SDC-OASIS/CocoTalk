//
//  ViewController.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/06.
//

import UIKit
import SnapKit
import Then
import RxSwift
import SwiftKeychainWrapper

class SplashViewController: UIViewController {
    
    // MARK: - UI Properties
    private let lblHello = UILabel().then {
        $0.text = "CocoTalk"
        $0.font = .systemFont(ofSize: 36)
    }
    
    // MARK: - Properties
    var viewModel = SplashViewModel()
    var bag = DisposeBag()

    // MARK: - Life cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .white
        
        view.addSubview(lblHello)
        lblHello.snp.makeConstraints {
            $0.center.equalToSuperview()
        }
        
        bind()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        if viewModel.dependency.isSignedIn {
            viewModel.verifyToken()
        } else {
            move2signInVC()
        }
    }
    
    // MARK: - Helper
    private func move2Home() {
        let root = RootTabBarController()
        switchRoot(to: root)
    }
    
    private func move2signInVC() {
        resetKeys()
        let signInVC = SigninViewController()
        let root = UINavigationController(rootViewController: signInVC)
        switchRoot(to: root)
    }
    
    private func switchRoot(to root: UIViewController) {
        setNeedsStatusBarAppearanceUpdate()
        view.window?.rootViewController = root
        view.window?.makeKeyAndVisible()
    }
    
    private func resetKeys() {
        KeychainWrapper.standard.remove(forKey: .fcmToken)
        KeychainWrapper.standard.remove(forKey: .accessToken)
        KeychainWrapper.standard.remove(forKey: .refreshToken)
    }
    
    private func bind() {
        viewModel.dependency.isValidToken
            .subscribe(onNext: { [weak self] isValidToken in
                guard let self = self,
                      let isValidToken = isValidToken else {
                          return
                      }
                if isValidToken {
                    self.move2Home()
                } else {
                    self.move2signInVC()
                }
            }).disposed(by: bag)
    }
}

