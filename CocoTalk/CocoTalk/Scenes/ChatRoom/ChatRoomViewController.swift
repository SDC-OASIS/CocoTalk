//
//  ChatRoomViewController.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/19.
//


import UIKit
import SnapKit
import Then
import RxSwift

class ChatRoomViewController: UIViewController {
    
    // MARK: - UI Properties
    private let lbl = UILabel().then {
        $0.text = "This is chat room."
    }
    
    /// 안내 문구
    ///
    /// 단순채팅
    ///
    /// 메시지 컨텐트 뷰
    ///
    ///
    
    // MARK: - Properties
    
    // MARK: - Life cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .yellow
        
        configureView()
        configureSubviews()
        bindRx()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        navigationController?.tabBarController?.tabBar.isHidden = true
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        navigationController?.tabBarController?.tabBar.isHidden = false
    }
    // MARK: - Helper
}

// MARK: - BaseViewController
extension ChatRoomViewController {
    func configureView() {
        view.addSubview(lbl)
    }
    
    func configureSubviews() {
        lbl.snp.makeConstraints {
            $0.center.equalToSuperview()
        }
    }
}

// MARK: - Bindable
extension ChatRoomViewController {
    func bindRx() {}
}
