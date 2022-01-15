//
//  NewProfileViewController.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/10.
//

import UIKit
import SnapKit
import Then
import RxSwift

class NewProfileViewController: UIViewController {
    
    // MARK: - UI Properties
    
    private let ivMask = UIImageView(image: UIImage(named: "squircle_mask")!)
    /// 프로필 이미지 버튼
    /// 1. 앨범에서 선택
    /// 2. 기본 이미지로 설정
    private let ivProfile = UIImageView(image: UIImage(named: "profile_camera")!)
    
    /// 이름 텍스트 필드
    private let textFieldUsername = UITextField().then {
        $0.placeholder = "이름 (필수)"
        $0.autocorrectionType = .no
        $0.autocapitalizationType = .none
        $0.spellCheckingType = .no
    }
    
    /// 주소록 친구 자동 추가 체크 박스
    private let ivCheckBox = UIImageView().then {
        $0.image = UIImage.init(systemName: "checkmark.circle")!
    }
    
    /// 주소록 친구 자동 추가 라벨
    private let lblAutoAddingFirends = UILabel().then {
        $0.text = "주소록 친구 자동 추가"
        $0.font = .systemFont(ofSize: 14)
        $0.textColor = .secondaryLabel
    }
    
    /// 확인 버튼
    private let btnConfirm = UIButton().then {
        $0.setTitle("확인", for: .normal)
        $0.backgroundColor = .systemBlue
    }
    
    
    // MARK: - Properties
    
    // MARK: - Life cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .white
        
        configureView()
        configureSubviews()
        bindRx()
    }
    
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        ivMask.frame = ivProfile.bounds
    }
    // MARK: - Helper
}

// MARK: - BaseViewController
extension NewProfileViewController {
    func configureView() {
        [ivProfile, textFieldUsername, ivCheckBox, lblAutoAddingFirends, btnConfirm].forEach {
            view.addSubview($0)
        }
    }
    
    func configureSubviews() {
        ivProfile.snp.makeConstraints {
            $0.centerX.equalToSuperview()
            $0.top.equalTo(view.safeAreaLayoutGuide).offset(30)
            $0.width.height.equalTo(100)
        }
        ivProfile.mask = ivMask
        
        textFieldUsername.snp.makeConstraints {
            $0.centerX.equalToSuperview()
            $0.top.equalTo(ivProfile.snp.bottom).offset(30)
            $0.leading.equalToSuperview().offset(30)
            $0.trailing.equalToSuperview().inset(30)
            $0.height.equalTo(30)
        }
        
        ivCheckBox.snp.makeConstraints {
            $0.leading.equalTo(textFieldUsername)
            $0.top.equalTo(textFieldUsername.snp.bottom).offset(8)
            $0.height.width.equalTo(26)
        }
        
        lblAutoAddingFirends.snp.makeConstraints {
            $0.centerY.equalTo(ivCheckBox)
            $0.leading.equalTo(ivCheckBox.snp.trailing).offset(8)
            $0.trailing.equalTo(textFieldUsername)
        }
        
        btnConfirm.snp.makeConstraints {
            $0.top.equalTo(textFieldUsername.snp.bottom).offset(80)
            $0.leading.trailing.equalTo(textFieldUsername)
            $0.height.equalTo(44)
        }
    }
}

// MARK: - Bindable
extension NewProfileViewController {
    func bindRx() {
        bindButton()
    }
    
    func bindButton() {
        
    }
}
