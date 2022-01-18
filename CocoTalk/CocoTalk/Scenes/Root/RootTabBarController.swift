//
//  RootViewController.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/17.
//

import UIKit
import SnapKit
import RxSwift

class RootTabBarController: UITabBarController {
    
    // MARK: - UI Properties
    
    
    // MARK: - Properties
    
    // MARK: - Life cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        
        configureView()
        configureSubviews()
        bindRx()
    }
    
    // MARK: - Helper
    private func createVC(for rootVC: UIViewController, title: String, inactivatedImage: UIImage?, activatedImage: UIImage?) -> UIViewController {
        let vc = UINavigationController(rootViewController: rootVC)
        let tabBarItem = UITabBarItem(title: title, image: inactivatedImage?.withRenderingMode(.automatic), selectedImage: activatedImage?.withRenderingMode(.automatic))
        vc.tabBarItem = tabBarItem
        return vc
    }
}

// MARK: - BaseViewController
extension RootTabBarController {
    
    func configureView() {
        tabBar.tintColor = .black
        UITabBar.appearance().backgroundColor = .secondarySystemFill
        tabBar.isTranslucent = false
        
        viewControllers = [
            createVC(for: FriendListViewController(),
                        title: "친구",
                        inactivatedImage: UIImage(systemName: "person"),
                        activatedImage: UIImage(systemName: "person.fill")),
            createVC(for: SigninViewController(),
                        title: "로그인",
                        inactivatedImage: UIImage(systemName: "lock"),
                        activatedImage: UIImage(systemName: "lock.fill"))
        ]
    }
    
    func configureSubviews() {
    }
}

// MARK: - Bindable
extension RootTabBarController {
    func bindRx() {}
}
