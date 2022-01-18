//
//  ViewController.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/06.
//

import UIKit
import SnapKit
import Then

class SplashViewController: UIViewController {
    
    // MARK: - UI Properties
    private let lblHello = UILabel().then {
        $0.text = "CocoTalk"
        $0.font = .systemFont(ofSize: 36)
    }

    // MARK: - Life cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .white
        
        view.addSubview(lblHello)
        lblHello.snp.makeConstraints {
            $0.center.equalToSuperview()
        }
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
        let isSignedIn = true
        
        if isSignedIn {
            move2root()
        } else {
            
        }
    }

    // MARK: - Helper
    #warning("로그인 여부에 따라 뷰컨트롤러 다르게 주기")
    #warning("UserDefault로 확인")
    private func move2root() {
//        let signInVC = SigninViewController()
//        let root = UINavigationController(rootViewController: signInVC)
        
        let root = RootTabBarController()
        
        setNeedsStatusBarAppearanceUpdate()
        view.window?.rootViewController = root
        view.window?.makeKeyAndVisible()
    }

}

