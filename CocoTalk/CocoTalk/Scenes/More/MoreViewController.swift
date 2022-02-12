//
//  MoreViewController.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/10.
//

import UIKit
import SnapKit
import Then
import RxSwift
import SwiftKeychainWrapper

class MoreViewController: UIViewController {
    
    // MARK: - UI Properties
    /// 네비게이션 바
    private let gnbView = GNBView()
    
    private let lblHello = UILabel().then {
        $0.text = "더보기"
    }
    
    private let btnSignout = UIButton().then {
        $0.setTitle("로그아웃", for: .normal)
        $0.setTitleColor(.black, for: .normal)
        $0.titleLabel?.font = .systemFont(ofSize: 20)
        $0.tintColor = .systemGreen
    }
    
    // MARK: - Properties
    let bag = DisposeBag()
    
    // MARK: - Life cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .white
        
        navigationController?.isNavigationBarHidden = true
        gnbView.setDelegate(delegate: self)
        
        configureView()
        configureSubviews()
        bind()
    }
    
    // MARK: - Helper
    private func signout() {
        KeychainWrapper.resetKeys()
        let signInVC = SigninViewController()
        let root = UINavigationController(rootViewController: signInVC)
        switchRoot(to: root)
    }
}

// MARK: - BaseViewController
extension MoreViewController {
    func configureView() {
        view.addSubview(gnbView)
        view.addSubview(btnSignout)
    }
    
    func configureSubviews() {
        gnbView.snp.makeConstraints {
            $0.top.leading.trailing.equalToSuperview()
            $0.height.equalTo(100)
        }
        
        btnSignout.snp.makeConstraints {
            $0.center.equalToSuperview()
        }
    }
}

// MARK: - Bindable
extension MoreViewController {
    func bind() {
        bindButton()
    }
    
    func bindButton() {
        btnSignout.rx.tap
            .subscribe(onNext: { [weak self] _ in
                guard let self = self else {
                    return
                }
                self.signout()
            }).disposed(by: bag)
    }
}

// MARK: - GNBDelegate
extension MoreViewController: GNBDelegate {
    func gnbTabType() -> TabEnum {
        return .more
    }
    
    func tapQR() {
        print("tap QR")
    }
    
    func tapSearch() {
        print("search")
    }
    
    func tapSetting() {
        print("setting")
    }
}
