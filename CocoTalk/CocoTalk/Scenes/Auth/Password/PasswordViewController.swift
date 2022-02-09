//
//  PasswordViewController.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/10.
//

import UIKit
import SnapKit
import Then
import RxSwift

class PasswordViewController: UIViewController {
    
    // MARK: - UI Properties
    private let lblNewAccount = UILabel().then {
        $0.text = "새로운 코코톡계정을 생성합니다"
        $0.font = .systemFont(ofSize: 18, weight: .bold)
        $0.textAlignment = .center
    }
    
    private let lblMyAccount = UILabel().then {
        $0.text = "내 코코톡 계정"
        $0.font = .systemFont(ofSize: 14)
        $0.textColor = .secondaryLabel
        $0.textAlignment = .center
    }
    
    private let lblPhoneNumber = UILabel().then {
        $0.text = "000-0000-0000"
        $0.font = .systemFont(ofSize: 24)
        $0.textAlignment = .center
    }
    
    private let lblId = UILabel().then {
        $0.text = "아이디"
        $0.font = .systemFont(ofSize: 14)
        $0.textColor = .label
        $0.textAlignment = .natural
    }
    
    private let textFieldId = UITextField().then {
        $0.placeholder = "ID (6자 이상)"
        $0.autocorrectionType = .no
        $0.autocapitalizationType = .none
        $0.spellCheckingType = .no
    }
    
    private let lblPassword = UILabel().then {
        $0.text = "비밀번호"
        $0.font = .systemFont(ofSize: 14)
        $0.textColor = .label
        $0.textAlignment = .natural
    }
    
    private let textFieldPassword = UITextField().then {
        $0.placeholder = "비밀번호 (8자 이상)"
        $0.autocorrectionType = .no
        $0.autocapitalizationType = .none
        $0.spellCheckingType = .no
        $0.textContentType = .password
        $0.isSecureTextEntry = true
    }
    
    private let textFieldConfirmPassword = UITextField().then {
        $0.placeholder = "비밀번호 확인 (8자 이상)"
        $0.autocorrectionType = .no
        $0.autocapitalizationType = .none
        $0.spellCheckingType = .no
        $0.textContentType = .password
        $0.isSecureTextEntry = true
    }
    
    private let lblWarning = UILabel().then {
        $0.text = "비밀번호가 일치하지 않습니다."
        $0.textColor = .systemRed
        $0.font = .systemFont(ofSize: 14)
        $0.isHidden = false
    }
    
    private let btnConfirm = UIButton().then {
        $0.setTitle("확인", for: .normal)
        $0.backgroundColor = .systemGreen
    }
    
    // MARK: - Properties
    var bag = DisposeBag()
    var viewModel = PasswordViewModel()
    var signupData: ModelSignupData?
    
    // MARK: - Life cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .white
        navigationItem.hidesBackButton = true
        
        #warning("암호화 보안이 필요하다")
        #warning("UserDefault extension에 completion handler로 빼기")
        if let savedData = UserDefaults.standard.object(forKey: UserDefaultsKey.signupData.rawValue) as? Data,
           let signupData = try? JSONDecoder().decode(ModelSignupData.self, from: savedData) {
            self.signupData = signupData
            lblPhoneNumber.text = signupData.phone
        }
        
        configureView()
        configureSubviews()
        bindRx()
    }
    
    // MARK: - Helper
    private func isValidPassword() -> Bool {
        return textFieldPassword.text?.count ?? 0 > 7
    }
    
    private func checkButton() {
        if textFieldPassword.text?.count ?? 0 > 7,
           textFieldPassword.text == textFieldConfirmPassword.text {
            lblWarning.isHidden = true
            btnConfirm.backgroundColor = .systemGreen
        } else {
            lblWarning.isHidden = false
            btnConfirm.backgroundColor = .systemGray
        }
    }
    
    private func pushNewProfileVC() {
        guard let password = textFieldPassword.text,
              let id = textFieldId.text,
              var signupData = self.signupData else {
                  return
              }
        signupData.password = password
        signupData.cid = id
        UserDefaults.standard.set(signupData.encode() ?? nil, forKey: UserDefaultsKey.signupData.rawValue)
        
        let vc = NewProfileViewController()
        navigationController?.pushViewController(vc, animated: true)
    }
    
    private func showAlert() {
        let alert = UIAlertController(title: "ID 오류", message: "이미 사용중인 ID 입니다.", preferredStyle: .alert)
        let okAction = UIAlertAction(title: "확인", style: .cancel)
        alert.addAction(okAction)
        present(alert, animated: true)
    }
}

// MARK: - BaseViewController
extension PasswordViewController {
    func configureView() {
        [lblNewAccount, lblMyAccount, lblPhoneNumber, lblId, textFieldId, lblPassword, textFieldPassword, textFieldConfirmPassword, lblWarning, btnConfirm].forEach {
            view.addSubview($0)
        }
    }
    
    func configureSubviews() {
        lblNewAccount.snp.makeConstraints {
            $0.centerX.equalToSuperview()
            $0.top.equalTo(view.safeAreaLayoutGuide).offset(30)
        }
        
        lblMyAccount.snp.makeConstraints {
            $0.centerX.equalToSuperview()
            $0.top.equalTo(lblNewAccount.snp.bottom).offset(30)
        }
        
        lblPhoneNumber.snp.makeConstraints {
            $0.centerX.equalToSuperview()
            $0.top.equalTo(lblMyAccount.snp.bottom).offset(8)
        }
        
        lblId.snp.makeConstraints {
            $0.top.equalTo(lblPhoneNumber.snp.bottom).offset(30)
            $0.leading.equalToSuperview().offset(30)
            $0.trailing.equalToSuperview().inset(30)
        }
        
        textFieldId.snp.makeConstraints {
            $0.top.equalTo(lblId.snp.bottom).offset(8)
            $0.leading.trailing.equalTo(lblId)
            $0.height.equalTo(30)
        }
        
        lblPassword.snp.makeConstraints {
            $0.top.equalTo(textFieldId.snp.bottom).offset(20)
            $0.leading.trailing.equalTo(lblId)
        }
        
        textFieldPassword.snp.makeConstraints {
//            $0.centerX.equalToSuperview()
            $0.top.equalTo(lblPassword.snp.bottom).offset(8)
            $0.leading.trailing.height.equalTo(lblId)
        }
        
        textFieldConfirmPassword.snp.makeConstraints {
            $0.top.equalTo(textFieldPassword.snp.bottom).offset(8)
            $0.leading.trailing.height.equalTo(lblId)
        }
        
        lblWarning.snp.makeConstraints {
            $0.leading.trailing.equalTo(lblId)
            $0.top.equalTo(textFieldConfirmPassword.snp.bottom).offset(8)
        }
        
        btnConfirm.snp.makeConstraints {
            $0.top.equalTo(textFieldConfirmPassword.snp.bottom).offset(50)
            $0.leading.trailing.equalTo(lblId)
            $0.height.equalTo(44)
        }
    }
}

// MARK: - Bindable
extension PasswordViewController {
    func bindRx() {
        bindButton()
        bindTextFields()
        bindViewModel()
    }
    
    func bindButton() {
        btnConfirm.rx.tap
            .subscribe(onNext: { [weak self] _ in
                guard let self = self,
                      self.isValidPassword(),
                      self.textFieldConfirmPassword.text == self.textFieldPassword.text else {
                          return
                      }
                if !self.viewModel.dependency.isLoading.value {
                    self.viewModel.checkIdDuplicated()
                }
            }).disposed(by: bag)
    }
    
    func bindTextFields() {
        textFieldId.rx.text
            .orEmpty
            .subscribe(onNext: { [weak self] text in
                guard let self = self else {
                    return
                }
                self.viewModel.input.idText.accept(text)
            }).disposed(by: bag)
        
        textFieldPassword.rx.text
            .orEmpty
            .subscribe(onNext: { [weak self] _ in
                guard let self = self else {
                    return
                }
                self.checkButton()
            }).disposed(by: bag)
        
        textFieldConfirmPassword.rx.text
            .orEmpty
            .subscribe(onNext: { [weak self] _ in
                guard let self = self else {
                    return
                }
                self.checkButton()
            }).disposed(by: bag)
    }
    
    func bindViewModel() {
        viewModel.dependency.validId
            .subscribe(onNext: { [weak self] result in
                guard let self = self,
                      let result = result else {
                          return
                      }
                
                if result {
                    self.pushNewProfileVC()
                } else {
                    self.showAlert()
                }
            }).disposed(by: bag)
    }
}
