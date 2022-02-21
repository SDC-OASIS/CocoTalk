//
//  SmsAuthViewController.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/11.
//


import UIKit
import SnapKit
import Then
import RxSwift
import FirebaseAuth

/// sms 인증 코드를 입력하는 뷰컨트롤러
class SmsAuthViewController: UIViewController {
    
    // MARK: - UI Properties
    /// 전화 번호
    private let lblPhoneNumber = UILabel().then {
        $0.text = "000-0000-0000"
        $0.font = .systemFont(ofSize: 24, weight: .bold)
    }
    
    /// 인증번호 텍스트 필드
    private let textFieldAuthNumber = UITextField().then {
        $0.placeholder = "인증번호 6자리"
        $0.textAlignment = .center
        $0.font = .systemFont(ofSize: 24)
        $0.textContentType = .oneTimeCode
        $0.keyboardType = .numberPad
    }
    
    /// 인증번호 만료 시간
    private let lblAuthExpireTime = UILabel().then {
        $0.font = .systemFont(ofSize: 14)
    }
    
    /// 확인 버튼
    private let btnConfirm = UIButton().then {
        $0.setTitle("확인", for: .normal)
        $0.backgroundColor = .systemBlue
    }
    
    /// 전화번호 변경하기 버튼 (이전 슬라이드로)
    private let btnChangePhoneNumber = UIButton().then {
        $0.setTitle("전화번호 변경하기", for: .normal)
        $0.setTitleColor(.secondaryLabel, for: .normal)
        $0.backgroundColor = .clear
    }
    
    #warning("재요청 보내기 버튼 생성")
    
    // MARK: - Properties
    let bag = DisposeBag()
    let viewModel = SmsAuthViewModel()
    let phoenNumber: String
    
    // MARK: - Life cycle
    init(phoneNumber: String) {
        self.phoenNumber = phoneNumber
        super.init(nibName: nil, bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .white
        navigationItem.hidesBackButton = true
        
        title = "문자 인증"
        
        lblPhoneNumber.text = phoenNumber.pretty()
        viewModel.dependency.phoneNumber = phoenNumber
        
        configureView()
        configureSubviews()
        bindRx()
        startTimer()
    }
    
    // MARK: - Helper
    private func verifyAuthNumber() {
        let verificationID = UserDefaults.standard.string(forKey: "authVerificationID")
        
        let credential = PhoneAuthProvider.provider().credential(
            withVerificationID: verificationID ?? "",
            verificationCode: textFieldAuthNumber.text ?? ""
        )
        
        Auth.auth().signIn(with: credential) { [weak self] authResult, error in
            guard let self = self else {
                return
            }
            
            if error == nil {
                self.pushPasswordVC()
            } else {
                self.authenticationFailAlert(title: "인증 실패", message: "잘못된 코드입니다.")
            }
        }
    }
    
    private func startTimer() {
        viewModel.dependency.timer = Timer.scheduledTimer(timeInterval: 1.0, target: self, selector: #selector(updateCounter), userInfo: nil, repeats: true)
    }
    
    @objc func updateCounter() {
        var count = viewModel.dependency.countDown.value
        if count > 0 {
            lblAuthExpireTime.text = "인증번호는 \(count)초 이후에 다시 요청할 수 있습니다."
            lblAuthExpireTime.textColor = .red
            count -= 1
            viewModel.dependency.countDown.accept(count)
        } else {
            authenticationFailAlert(title: "인증 오류", message: "문자인증에 실패했습니다.")
        }
    }
    
    private func authenticationFailAlert(title: String, message: String) {
        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        let okAction = UIAlertAction(title: "확인", style: .cancel) { _ in
            self.viewModel.dependency.timer?.invalidate()
            self.navigationController?.popViewController(animated: true)
        }
        alert.addAction(okAction)
        present(alert, animated: true)
    }
    
    private func pushPasswordVC() {
        let vc = PasswordViewController()
        viewModel.dependency.timer?.invalidate()
        navigationController?.pushViewController(vc, animated: true)
    }
}

// MARK: - BaseViewController
extension SmsAuthViewController {
    func configureView() {
        [lblPhoneNumber, textFieldAuthNumber, lblAuthExpireTime, btnConfirm, btnChangePhoneNumber].forEach {
            view.addSubview($0)
        }
    }
    
    func configureSubviews() {
        lblPhoneNumber.snp.makeConstraints {
            $0.centerX.equalToSuperview()
            $0.top.equalTo(view.safeAreaLayoutGuide).offset(30)
        }
        
        textFieldAuthNumber.snp.makeConstraints {
            $0.centerX.equalToSuperview()
            $0.top.equalTo(lblPhoneNumber.snp.bottom).offset(40)
            $0.leading.equalToSuperview().offset(30)
            $0.trailing.equalToSuperview().inset(30)
        }
        
        lblAuthExpireTime.snp.makeConstraints {
            $0.top.equalTo(textFieldAuthNumber.snp.bottom).offset(8)
            $0.leading.trailing.equalTo(textFieldAuthNumber)
        }
        
        btnConfirm.snp.makeConstraints {
            $0.top.equalTo(textFieldAuthNumber.snp.bottom).offset(40)
            $0.leading.trailing.equalTo(textFieldAuthNumber)
            $0.height.equalTo(44)
        }
        
        btnChangePhoneNumber.snp.makeConstraints {
            $0.top.equalTo(btnConfirm.snp.bottom).offset(10)
            $0.leading.trailing.equalTo(textFieldAuthNumber)
        }
    }
}

// MARK: - Bindable
extension SmsAuthViewController {
    func bindRx() {
        bindButton()
        bindTextField()
        bindViewModel()
    }
    
    func bindButton() {
        btnConfirm.rx.tap
            .subscribe(onNext: { [weak self] _ in
                guard let self = self,
                      let isLoading = self.viewModel.dependency.isLoading.value  else {
                    return
                }
                
                if !isLoading,
                   self.textFieldAuthNumber.text?.count == 6 {
                    self.viewModel.checkPhoneExist()
                }
            }).disposed(by: bag)
        
        btnChangePhoneNumber.rx.tap
            .subscribe(onNext: { [weak self] _ in
                guard let self = self,
                      let navigationController = self.navigationController  else {
                          return
                      }
                navigationController.popViewController(animated: true)
            }).disposed(by: bag)
    }
    
    func bindTextField() {
        textFieldAuthNumber.rx.text
            .orEmpty
            .scan("") { [weak self] oldValue, newValue in
                guard let self = self else {
                    return oldValue
                }
                
                var result: String
                if newValue.isNumber() && newValue.count < 7 {
                    if newValue.count == 6 {
                        self.btnConfirm.backgroundColor = .systemGreen
                    } else {
                        self.btnConfirm.backgroundColor = .systemGray
                    }
                    result = newValue
                } else {
                    if oldValue.count == 6 {
                        self.btnConfirm.backgroundColor = .systemGreen
                    } else {
                        self.btnConfirm.backgroundColor = .systemGray
                    }
                    result = oldValue
                }
                
                self.viewModel.input.smsAuthCode.accept(result)
                return result
            }.bind(to: textFieldAuthNumber.rx.text)
            .disposed(by: bag)
    }
    
    private func bindViewModel() {
        viewModel.dependency.isPhoneExist
            .subscribe(onNext: { [weak self] result in
                guard let self = self,
                      let result = result else {
                    return
                }
                if !result {
                    self.verifyAuthNumber()
                } else {
                    self.authenticationFailAlert(title: "중복된 전화번호", message: "이미 ID가 존재하는 전화번호입니다.")
                }
            }).disposed(by: bag)
    }
}
