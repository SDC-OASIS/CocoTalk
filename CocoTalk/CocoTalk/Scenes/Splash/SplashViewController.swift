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

/// 스플래시 뷰
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
            initSockets()
            move2Home()
//            viewModel.verifyToken()
        } else {
            move2signInVC()
        }
    }
    
    // MARK: - Helper
    func initSockets() {
        let appDelegate = UIApplication.shared.delegate as? AppDelegate
        appDelegate?.initListSocket()
        appDelegate?.establishChatSocketConnection()
    }
    
    private func move2Home() {
        let root = RootTabBarController()
        switchRoot(to: root)
    }
    
    private func move2signInVC() {
        KeychainWrapper.resetKeys()
        let signInVC = SigninViewController()
        let root = UINavigationController(rootViewController: signInVC)
        switchRoot(to: root)
    }
    
    private func bind() {
//        viewModel.dependency.shouldSignout
//            .subscribe(onNext: { [weak self] shouldSignout in
//                guard let self = self,
//                      let shouldSignout = shouldSignout else {
//                          return
//                      }
//                if !shouldSignout {
//                    self.move2Home()
//                } else {
//                    self.move2signInVC()
//                }
//            }).disposed(by: bag)
//
//        viewModel.dependency.isValidToken
//            .subscribe(onNext: { [weak self] isValid in
//                guard let self = self,
//                      let isValid = isValid else {
//                    return
//                }
//
//                if !isValid {
//                    self.viewModel.reissueToken()
//                } else {
//                    self.move2Home()
//                }
//            }).disposed(by: bag)
    }
}

