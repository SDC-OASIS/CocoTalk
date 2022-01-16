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
        $0.backgroundColor = .systemBlue
    }
    
    /// 인증 번호 보내기 모달
    #warning("UIKit 모달로 수정")
    private let confirmModal = ConfirmModalView().then {
        $0.isHidden = true
    }
    
    // MARK: - Properties
    let bag = DisposeBag()
    
    // MARK: - Life cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .white
        title = "전화번호를 입력해 주세요"
        
        configureView()
        configureSubviews()
        bindRx()
    }
    
    // MARK: - Helper
}

// MARK: - BaseViewController
extension PhoneNumberViewController {
    func configureView() {
        [textFieldPhoneNumber, btnConfirm, confirmModal].forEach {
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
        
        confirmModal.snp.makeConstraints {
            $0.edges.equalToSuperview()
        }
    }
}

// MARK: - Bindable
extension PhoneNumberViewController {
    func bindRx() {
        bindButton()
    }
    
    func bindButton() {
        confirmModal.btnConfirm.rx.tap
            .subscribe(onNext: { [weak self] _ in
                guard let self = self else { return }
                self.confirmModal.isHidden = true
                let vc = SmsAuthViewController()
                self.navigationController?.pushViewController(vc, animated: true)
            }).disposed(by: confirmModal.bag)
        
        btnConfirm.rx.tap
            .subscribe(onNext: { [weak self] _ in
                guard let self = self else { return }
                self.confirmModal.isHidden = false
            }).disposed(by: bag)
    }
}
