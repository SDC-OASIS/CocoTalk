//
//  EmailAuthNumberViewController.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/16.
//



import UIKit
import SnapKit
import Then
import RxSwift

/// 이메일 인증코드를 입력하는 뷰
class EmailAuthNumberViewController: UIViewController {
    
    // MARK: - UI Properties
    /// Guide label
    private let lblGuide = UILabel().then {
        $0.text = "아래 이메일로\n인증번호를 보냈습니다"
        $0.font = .systemFont(ofSize: 24)
        $0.numberOfLines = 0
        $0.textAlignment = .center
    }
    
    /// 이메일 라벨
    private let lblEmail = UILabel().then {
        $0.font = .systemFont(ofSize: 18, weight: .semibold)
        $0.textAlignment = .center
    }
    
    /// 인증번호 텍스트 필드
    private let textFieldAuthNumber = UITextField().then {
        $0.placeholder = "인증코드"
        $0.textAlignment = .center
        $0.font = .systemFont(ofSize: 24)
        $0.textContentType = .oneTimeCode
    }
    
    /// 인증메일을 받지 못하셨나요? 라벨
    private let lblNotice = UILabel().then {
        let text = "인증메일을 받지 못하셨나요?"
        let attributedString = NSMutableAttributedString.init(string: "\(text)")
        attributedString.addAttribute(NSAttributedString.Key.underlineStyle, value: 1, range: NSRange.init(location: 0, length: text.count))
        $0.attributedText = attributedString
        $0.textColor = .secondaryLabel
        $0.font = .systemFont(ofSize: 14)
    }
    
    /// 확인 버튼
    private let btnConfirm = UIButton().then {
        $0.setTitle("확인", for: .normal)
        $0.backgroundColor = .systemGreen
    }
    
    
    // MARK: - Properties
    let bag = DisposeBag()
    let viewModel = EmailAuthViewModel()
    var signupData: ModelSignupData?
    
    // MARK: - Life cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .white
        
        if let savedData = UserDefaults.standard.object(forKey: UserDefaultsKey.signupData.rawValue) as? Data,
           let data = try? JSONDecoder().decode(ModelSignupData.self, from: savedData) {
            self.signupData = data
            lblEmail.text = data.email
            self.viewModel.dependency.signupData = data
        }
        
        configureView()
        configureSubviews()
        bindRx()
    }
    
    // MARK: - Helper
    private func showAlert(title: String, message: String) {
        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "확인", style: .default){ _ in
            self.navigationController?.popViewController(animated: true)
        })
        present(alert, animated: true)
    }
    
    private func move2Home() {
        let root = RootTabBarController()
        setNeedsStatusBarAppearanceUpdate()
        view.window?.rootViewController = root
        view.window?.makeKeyAndVisible()
    }
}

// MARK: - BaseViewController
extension EmailAuthNumberViewController {
    func configureView() {
        [lblGuide, lblEmail, textFieldAuthNumber, lblNotice, btnConfirm].forEach {
            view.addSubview($0)
        }
    }
    
    func configureSubviews() {
        lblGuide.snp.makeConstraints {
            $0.top.equalTo(view.safeAreaLayoutGuide).offset(30)
            $0.leading.equalToSuperview().offset(30)
            $0.trailing.equalToSuperview().inset(30)
        }
        
        lblEmail.snp.makeConstraints {
            $0.top.equalTo(lblGuide.snp.bottom).offset(20)
            $0.leading.trailing.equalTo(lblGuide)
        }
        
        textFieldAuthNumber.snp.makeConstraints {
            $0.top.equalTo(lblEmail.snp.bottom).offset(30)
            $0.leading.trailing.equalTo(lblGuide)
        }
        
        lblNotice.snp.makeConstraints {
            $0.top.equalTo(textFieldAuthNumber.snp.bottom).offset(10)
            $0.leading.equalTo(lblGuide)
        }
        
        btnConfirm.snp.makeConstraints {
            $0.top.equalTo(textFieldAuthNumber.snp.bottom).offset(70)
            $0.leading.trailing.equalTo(lblGuide)
            $0.height.equalTo(44)
        }
    }
}

// MARK: - Bindable
extension EmailAuthNumberViewController {
    func bindRx() {
        bindButton()
        bindTextField()
        bindViewModel()
    }
    
    func bindButton() {
        btnConfirm.rx.tap
            .subscribe(onNext: { [weak self] _ in
                guard let self = self,
                      let data = self.signupData,
                      !self.viewModel.dependency.isLoading else {
                          return
                      }
                self.viewModel.verifyEmail(with: data.email)
            }).disposed(by: bag)
    }
    
    func bindTextField() {
        textFieldAuthNumber.rx.text
            .orEmpty
            .bind(to: viewModel.input.code)
            .disposed(by: bag)
    }
    
    func bindViewModel() {
        viewModel.dependency.isSigninComplete
            .subscribe(onNext: { [weak self] isCompleted in
                guard let self = self,
                      isCompleted,
                      !self.viewModel.dependency.isLoading else {
                          return
                      }
                self.move2Home()
            }).disposed(by: bag)
        
        viewModel.dependency.error
            .subscribe(onNext: { [weak self] error in
                guard let self = self,
                      let _ = error else {
                          return
                      }
                self.showAlert(title: "이메일 인증 오류", message: "올바르지 않은 인증번호 입니다.")
            }).disposed(by: bag)
        
        viewModel.dependency.isInvalidEmailError
            .subscribe(onNext: { [weak self] error in
                guard let self = self,
                      let _ = error else {
                          return
                      }
                self.showAlert(title: "이메일 인증 오류", message: "이미 사용중인 이메일입니다.")
            }).disposed(by: bag)
    }
}
