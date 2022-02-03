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

class SmsAuthViewController: UIViewController {
    
    // MARK: - UI Properties
    /// 전화 번호
    private let lblPhoneNumber = UILabel().then {
        $0.text = "000-0000-0000"
        $0.font = .systemFont(ofSize: 24, weight: .bold)
    }
    
    /// 인증번호 텍스트 필드
#warning("4자리 제한")
    private let textFieldAuthNumber = UITextField().then {
        $0.placeholder = "인증번호 4자리"
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
        
        configureView()
        configureSubviews()
        bindRx()
        startTimer()
    }
    
    // MARK: - Helper
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
            authenticationFailAlert()
            self.navigationController?.popViewController(animated: true)
        }
    }
    
    func authenticationFailAlert() {
        let alert = UIAlertController(title: "인증 실패", message: "문자인증에 실패했습니다.", preferredStyle: .alert)
        let okAction = UIAlertAction(title: "확인", style: .cancel) { [weak self] _ in
            guard let self = self else {
                return
            }
            self.pushPasswordVC()
        }
        alert.addAction(okAction)
        present(alert, animated: true)
    }
    
    func pushPasswordVC() {
        let vc = PasswordViewController()
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
    }
    
    func bindButton() {
        btnConfirm.rx.tap
            .subscribe(onNext: { [weak self] _ in
                guard let self = self else { return }
                self.authenticationFailAlert()
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
            .scan("") { oldValue, newValue in
                if newValue.isNumber() && newValue.count < 5 {
                    return newValue
                } else {
                    return oldValue
                }
            }.bind(to: textFieldAuthNumber.rx.text)
            .disposed(by: bag)
    }
}
