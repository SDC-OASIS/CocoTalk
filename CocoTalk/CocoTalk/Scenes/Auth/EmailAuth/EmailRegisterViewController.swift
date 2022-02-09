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
import RxCocoa

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
        $0.tintColor = .systemGreen
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
        $0.backgroundColor = .systemGreen
    }
    
    // MARK: - Properties
    var viewModel = EmailRegisterViewModel()
    let bag = DisposeBag()
    var isChecked = false
    
    // MARK: - Life cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .white
        navigationItem.hidesBackButton = true
        
        configureView()
        configureSubviews()
        bindRx()
    }
    
    // MARK: - Helper
    private func showEmailAlert() {
        let alert = UIAlertController(title: "이메일 오류", message: "이메일을 입력해주세요", preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "확인", style: .default))
        present(alert, animated: true)
    }
    
    private func showDuplicatedEmailAlert() {
        let alert = UIAlertController(title: "이메일 오류", message: "이미 사용중인 이메일 입니다.", preferredStyle: .alert)
        let okAction = UIAlertAction(title: "확인", style: .cancel)
        alert.addAction(okAction)
        present(alert, animated: true)
    }
    
    private func pushEmailAuthVC() {
        guard let email = textFieldEmail.text,
              !email.isEmpty  else {
                  showEmailAlert()
                  return
              }
        
        if let savedData = UserDefaults.standard.object(forKey: UserDefaultsKey.signupData.rawValue) as? Data,
           var signupData = ModelSignupData.decode(savedData: savedData) {
            signupData.email = email
            UserDefaults.standard.set(signupData.encode() ?? nil, forKey: UserDefaultsKey.signupData.rawValue)
        }
        
        let vc = EmailAuthNumberViewController()
        navigationController?.pushViewController(vc, animated: true)
    }
}

// MARK: - BaseViewController
extension EmailRegisterViewController {
    func configureView() {
        [lblTitle, textFieldEmail, ivCheckBox, lblPrivacy, lblNotice, btnSendAuthMail].forEach {
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
    }
}

// MARK: - Bindable
extension EmailRegisterViewController {
    func bindRx() {
        bindButton()
        bindTextField()
        bindViewModel()
    }
    
    func bindButton() {
        btnSendAuthMail.rx.tap
            .subscribe(onNext: { [weak self] _ in
                guard let self = self else {
                          return
                      }
                if !self.viewModel.dependency.isLoadingCheckEmail.value,
                   !self.viewModel.dependency.isLoadingRequestCode.value {
                    self.viewModel.checkEmailDuplicated()
                }
            }).disposed(by: bag)
        
        ivCheckBox.rx.tapGesture()
            .when(.recognized)
            .subscribe(onNext: { [weak self] _ in
                guard let self = self else {
                    return
                }
                self.isChecked.toggle()
                DispatchQueue.main.async {
                    if self.isChecked {
                        self.ivCheckBox.image = UIImage(systemName: "checkmark.circle.fill")!
                    } else {
                        self.ivCheckBox.image = UIImage(systemName: "checkmark.circle")!
                    }
                }
            }).disposed(by: bag)
    }
    
    func bindTextField() {
        textFieldEmail.rx.text
            .subscribe(onNext: { [weak self] text in
                guard let self = self else {
                    return
                }
                self.viewModel.input.email.accept(text ?? "")
            }).disposed(by: bag)
    }
    
    func bindViewModel() {
        viewModel.dependency.validEmail
            .subscribe(onNext: { [weak self] isValid in
                guard let self = self,
                      let isValid = isValid else {
                          return
                      }
                if isValid {
                    self.viewModel.requestEmailAuthenticationCode()
                } else  {
                    self.showDuplicatedEmailAlert()
                }
            }).disposed(by: bag)
        
        viewModel.dependency.requestSuccess
            .subscribe(onNext: { [weak self] result in
                guard let self = self,
                      let _ = result else {
                          return
                      }
                self.pushEmailAuthVC()
            }).disposed(by: bag)
    }
}
