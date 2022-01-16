//
//  EmailRegisterViewController.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/10.
//

import UIKit
import SnapKit
import Then
import RxSwift

class EmailRegisterViewController: UIViewController {
    
    // MARK: - UI Properties
    /// 이메일을 등록해 주세요 라벨
    private let lblTitle = UILabel().then {
        $0.text = "이메일을 등록해 주세요"
        $0.font = .systemFont(ofSize: 24)
    }
    
    /// 이메일 TextField
    private let textFieldEmail = UITextField().then {
        $0.placeholder = "이메일"
        $0.autocorrectionType = .no
        $0.autocapitalizationType = .none
        $0.spellCheckingType = .no
        $0.textContentType = .emailAddress
        $0.keyboardType = .emailAddress
    }
    
    /// 개인정보 체크 박스
    private let ivCheckBox = UIImageView().then {
        $0.image = UIImage.init(systemName: "checkmark.circle")!
    }
    
    /// 개인정보 라벨
    private let lblPrivacy = UILabel().then {
        $0.text = "개인정보 수집 및 이용 동의"
        $0.font = .systemFont(ofSize: 14)
        $0.textColor = .label
    }
    
    private let lblNotice = UILabel().then {
        $0.text = "동의하지 않아도 서비스 이용이 가능합니다."
        $0.font = .systemFont(ofSize: 12)
        $0.textColor = .secondaryLabel
    }
    
    /// 인증메일 발송 버튼
    private let btnSendAuthMail = UIButton().then {
        $0.setTitle("인증메일 발송", for: .normal)
        $0.backgroundColor = .systemBlue
    }
    
    /// 나중에 하기 버튼
    private let btnLater = UIButton().then {
        $0.setTitle("나중에 하기", for: .normal)
        $0.setTitleColor(.black, for: .normal)
        $0.backgroundColor = .clear
    }
    
    // MARK: - Properties
    let bag = DisposeBag()
    
    // MARK: - Life cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        
        view.backgroundColor = .white
        
        configureView()
        configureSubviews()
        bindRx()
    }
    
    // MARK: - Helper
}

// MARK: - BaseViewController
extension EmailRegisterViewController {
    func configureView() {
        [lblTitle, textFieldEmail, ivCheckBox, lblPrivacy, lblNotice, btnSendAuthMail, btnLater].forEach {
            view.addSubview($0)
        }
    }
    
    func configureSubviews() {
        lblTitle.snp.makeConstraints {
            $0.centerX.equalToSuperview()
            $0.top.equalTo(view.safeAreaLayoutGuide).offset(30)
        }
        
        textFieldEmail.snp.makeConstraints {
            $0.centerX.equalToSuperview()
            $0.top.equalTo(lblTitle.snp.bottom).offset(30)
            $0.leading.equalToSuperview().offset(30)
            $0.trailing.equalToSuperview().inset(30)
        }
        
        ivCheckBox.snp.makeConstraints {
            $0.leading.equalTo(textFieldEmail)
            $0.top.equalTo(textFieldEmail.snp.bottom).offset(8)
            $0.height.width.equalTo(26)
        }
        
        lblPrivacy.snp.makeConstraints {
            $0.centerY.equalTo(ivCheckBox)
            $0.leading.equalTo(ivCheckBox.snp.trailing).offset(8)
            $0.trailing.equalTo(textFieldEmail)
        }
        
        lblNotice.snp.makeConstraints {
            $0.top.equalTo(lblPrivacy.snp.bottom).offset(4)
            $0.leading.trailing.equalTo(lblPrivacy)
        }
        
        btnSendAuthMail.snp.makeConstraints {
            $0.top.equalTo(textFieldEmail.snp.bottom).offset(80)
            $0.leading.trailing.equalTo(textFieldEmail)
            $0.height.equalTo(44)
        }
        
        btnLater.snp.makeConstraints {
            $0.top.equalTo(btnSendAuthMail.snp.bottom).offset(10)
            $0.leading.trailing.height.equalTo(btnSendAuthMail)
        }
    }
}

// MARK: - Bindable
extension EmailRegisterViewController {
    func bindRx() {
        bindButton()
    }
    
    func bindButton() {
        btnSendAuthMail.rx.tap
            .subscribe(onNext: { [weak self] _ in
                guard let self = self,
                      let nav = self.navigationController else {
                          return
                      }
                #warning("이메일 인증 API 호출")
                let vc = EmailAuthNumberViewController()
                nav.pushViewController(vc, animated: true)
            }).disposed(by: bag)
        
        
    }
}
