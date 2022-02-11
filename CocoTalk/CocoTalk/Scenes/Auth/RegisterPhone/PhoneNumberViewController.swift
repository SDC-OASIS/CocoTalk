//
//  PhoneNumberViewController.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/10.
//

import UIKit
import SnapKit
import Then
import RxSwift

class PhoneNumberViewController: UIViewController {
    
    // MARK: - UI Properties
    /// 전화번호 입력 뷰
    private let textFieldPhoneNumber = UITextField().then {
        $0.placeholder = "전화번호를 입력해주세요"
        $0.autocorrectionType = .no
        $0.autocapitalizationType = .none
        $0.spellCheckingType = .no
        $0.textContentType = .telephoneNumber
        $0.keyboardType = .phonePad
    }
    
    /// 확인 버튼
    private let btnConfirm = UIButton().then {
        $0.setTitle("확인", for: .normal)
        $0.backgroundColor = .systemGreen
    }
    
    // MARK: - Properties
    let bag = DisposeBag()
    
    // MARK: - Life cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .white
        navigationItem.hidesBackButton = true
        
        title = "전화번호를 입력해 주세요"
        
        configureView()
        configureSubviews()
        bindRx()
    }
    
    // MARK: - Helper
    private func showConfirmAlert() {
        guard let phoneNumber = textFieldPhoneNumber.text else {
            return
        }
        
        let alertController = UIAlertController(title: "\(phoneNumber)", message: "\n이 전화번호로 인증번호를 보냅니다.\n\n전화번호를 수정하려면 아래 취소 버튼을 선택해주세요.", preferredStyle: .alert)
        
        alertController.addAction(UIAlertAction(title: "취소", style: .default, handler: nil))
        alertController.addAction(UIAlertAction(title: "확인", style: .default, handler: { [weak self] _ in
            guard let self = self else {
                return
            }
            self.move2SmsVC()
        }))
        
        present(alertController, animated: true)
    }
    
    private func move2SmsVC() {
        guard let phoneNumber = textFieldPhoneNumber.text else {
            return
        }
        
        #warning("SMS 인증 보내기")
    
        if let savedData = UserDefaults.standard.object(forKey: UserDefaultsKey.signupData.rawValue) as? Data,
           var signupData = try? JSONDecoder().decode(ModelSignupData.self, from: savedData) {
            signupData.phone = textFieldPhoneNumber.text ?? ""
            UserDefaults.standard.set(signupData.encode() ?? nil, forKey: UserDefaultsKey.signupData.rawValue)
        }
        let vc = SmsAuthViewController(phoneNumber: phoneNumber)
        self.navigationController?.pushViewController(vc, animated: true)
    }
}

// MARK: - BaseViewController
extension PhoneNumberViewController {
    func configureView() {
        [textFieldPhoneNumber, btnConfirm].forEach {
            view.addSubview($0)
        }
    }
    
    func configureSubviews() {
        textFieldPhoneNumber.snp.makeConstraints {
            $0.centerX.equalToSuperview()
            $0.top.equalTo(view.safeAreaLayoutGuide).offset(40)
            $0.leading.equalToSuperview().offset(20)
            $0.trailing.equalToSuperview().inset(20)
            $0.height.equalTo(40)
        }
        
        btnConfirm.snp.makeConstraints {
            $0.top.equalTo(textFieldPhoneNumber.snp.bottom).offset(30)
            $0.leading.trailing.equalTo(textFieldPhoneNumber)
            $0.height.equalTo(44)
        }
    }
}

// MARK: - Bindable
extension PhoneNumberViewController {
    func bindRx() {
        bindTextField()
        bindButton()
    }
    
    func bindButton() {
        btnConfirm.rx.tap
            .subscribe(onNext: { [weak self] _ in
                guard let self = self else { return }
                self.showConfirmAlert()
            }).disposed(by: bag)
    }
    
    func bindTextField() {
        textFieldPhoneNumber.rx.text
            .orEmpty
            .scan("") { oldValue, newValue in
                if newValue.isNumber() {
                    return newValue
                } else {
                    return oldValue
                }
            }.bind(to: textFieldPhoneNumber.rx.text)
            .disposed(by: bag)
    }
}
