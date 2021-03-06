//
//  SigninViewController.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/10.
//

import UIKit
import SnapKit
import Then
import RxSwift
import RxCocoa

/// 아이디로 로그인하는 뷰
class SigninViewController: UIViewController {
    
    // MARK: - UI Properties
    /// CocoTalk 환영 문구
    private let lblWelcome = UILabel().then {
        $0.text = "코코톡을 시작합니다"
        $0.font = .systemFont(ofSize: 24, weight: .bold)
    }
    
    /// 로그인 안내 문구
    private let lblNotice = UILabel().then {
        $0.text = "사용하던 코코톡계정이 있다면\n이메일 또는 전화번호로 로그인 해주세요."
        $0.font = .systemFont(ofSize: 16)
        $0.textColor = .secondaryLabel
        $0.numberOfLines = 0
        $0.textAlignment = .center
    }
    
    /// 코코톡 아이디 텍스트 필드
    private let textFieldEmail = UITextField().then {
        $0.placeholder = "코코톡 아이디"
        $0.autocorrectionType = .no
        $0.autocapitalizationType = .none
        $0.spellCheckingType = .no
    }
    
    /// 비밀번호 텍스트 필드
    private let textFieldPassword = UITextField().then {
        $0.placeholder = "비밀번호"
        $0.autocorrectionType = .no
        $0.autocapitalizationType = .none
        $0.spellCheckingType = .no
        $0.textContentType = .password
        $0.isSecureTextEntry = true
    }
    
    /// 로그인 버튼
    private let btnSignin = UIButton().then {
        $0.setTitle("코코톡 로그인", for: .normal)
        $0.backgroundColor = .systemGreen
    }
    
    /// 가입 버튼
    private let btnSignup = UIButton().then {
        $0.setTitle("새로운 코코톡계정 만들기", for: .normal)
        $0.setTitleColor(.label, for: .normal)
        $0.backgroundColor = .clear
    }
    
    /// 계정 및 비밀번호 찾기 버튼
    private let btnFindAccount = UIButton().then {
        $0.setTitle("코코톡 계정 또는 비밀번호 찾기", for: .normal)
        $0.setTitleColor(.label, for: .normal)
        $0.backgroundColor = .clear
        $0.isHidden = true
    }
    
    // MARK: - Properties
    let viewModel = SigninViewModel()
    let bag = DisposeBag()
    
    // MARK: - Life cycles
    override func viewDidLoad() {
        super.viewDidLoad()
        
        configureView()
        configureSubviews()
        bindRx()
    }
    
    // MARK: - Helper
    private func move2Home() {
        let root = RootTabBarController()
        setNeedsStatusBarAppearanceUpdate()
        view.window?.rootViewController = root
        view.window?.makeKeyAndVisible()
    }
    
    private func showAlert() {
        let alert = UIAlertController(title: "로그인 오류", message: nil, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "확인", style: .default){ [weak self] _ in
            guard let self = self else {
                return
            }
            self.viewModel.dependency.error.accept(false)
        })
        present(alert, animated: true)
    }
}

// MARK: - BaseViewController
extension SigninViewController {
    func configureView() {
        [lblWelcome, lblNotice, textFieldEmail, textFieldPassword, btnSignin, btnSignup, btnFindAccount].forEach {
            view.addSubview($0)
        }
    }
    
    func configureSubviews() {
        lblWelcome.snp.makeConstraints {
            $0.centerX.equalToSuperview()
            $0.top.equalTo(view.safeAreaLayoutGuide).offset(30)
        }
        
        lblNotice.snp.makeConstraints {
            $0.centerX.equalToSuperview()
            $0.top.equalTo(lblWelcome.snp.bottom).offset(30)
        }
        
        textFieldEmail.snp.makeConstraints {
            $0.centerX.equalToSuperview()
            $0.top.equalTo(lblNotice.snp.bottom).offset(30)
            $0.leading.equalToSuperview().offset(20)
            $0.trailing.equalToSuperview().inset(20)
            $0.height.equalTo(44)
        }
        
        textFieldPassword.snp.makeConstraints {
            $0.top.equalTo(textFieldEmail.snp.bottom).offset(10)
            $0.leading.trailing.height.equalTo(textFieldEmail)
        }
        
        btnSignin.snp.makeConstraints {
            $0.top.equalTo(textFieldPassword.snp.bottom).offset(30)
            $0.leading.trailing.height.equalTo(textFieldEmail)
        }
        
        btnSignup.snp.makeConstraints {
            $0.top.equalTo(btnSignin.snp.bottom).offset(20)
            $0.leading.trailing.height.equalTo(textFieldEmail)
        }
        
        btnFindAccount.snp.makeConstraints {
            $0.top.equalTo(btnSignup.snp.bottom).offset(10)
            $0.leading.trailing.height.equalTo(textFieldEmail)
        }
    }
}

// MARK: - Bindable ViewController
extension SigninViewController {
    func bindRx() {
        bindButton()
        bindTextField()
        bindViewModel()
    }
    
    private func bindButton() {
        btnSignin.rx.tap.bind { [weak self] in
            guard let self = self else {
                return
            }
            self.viewModel.signin()
        }.disposed(by: bag)
        
        btnSignup.rx.tap.bind { [weak self] in
            guard let self = self else { return }
            let vc = TermViewController()
            DispatchQueue.main.async {
                self.navigationController?.pushViewController(vc, animated: true)
            }
        }
        .disposed(by: bag)
    }

    private func bindTextField() {
        textFieldEmail.rx.text
            .orEmpty
            .subscribe(onNext: { [weak self] text in
                guard let self = self else {
                    return
                }
                self.viewModel.input.cid.accept(text)
            }).disposed(by: bag)
        
        textFieldPassword.rx.text
            .orEmpty
            .subscribe(onNext: { [weak self] text in
                guard let self = self else {
                    return
                }
                self.viewModel.input.password.accept(text)
            }).disposed(by: bag)
    }
    
    private func bindViewModel() {
        viewModel.dependency.isSigninComplete
            .subscribe(onNext: { [weak self] isCompleted in
                guard let self = self else {
                    return
                }
                
                if isCompleted {
                    let appDelegate = UIApplication.shared.delegate as? AppDelegate
                    appDelegate?.initListSocket()
                    self.move2Home()
                }
            }).disposed(by: bag)
        
        viewModel.dependency.error
            .subscribe(onNext: { [weak self] error in
                guard let self = self,
                      let error = error,
                      error else {
                          return
                      }
                self.showAlert()
            }).disposed(by: bag)
    }
}
