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
        $0.text = "email@cocotalk.com"
        $0.font = .systemFont(ofSize: 18, weight: .semibold)
        $0.textAlignment = .center
    }
    
    /// 인증번호 텍스트 필드
    private let textFieldAuthNumber = UITextField().then {
        $0.placeholder = "인증번호"
        $0.textAlignment = .center
        $0.font = .systemFont(ofSize: 24)
        $0.textContentType = .oneTimeCode
        $0.keyboardType = .numberPad
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
    
    // MARK: - Helper
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
    func bindRx() {}
}
