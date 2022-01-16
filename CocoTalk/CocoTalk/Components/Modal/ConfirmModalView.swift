//
//  ConfirmModalView.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/11.
//

import UIKit
import SnapKit
import RxSwift

class ConfirmModalView: UIView {
    
    // MARK: - UI Properties
    private let viewBackground = UIView().then {
        $0.backgroundColor = .init(red: 0, green: 0, blue: 0, alpha: 0.5)
    }
    
    private let viewModal = UIView().then {
        $0.backgroundColor = .white
        $0.layer.cornerRadius = 8
    }
    
    #warning("테스트 문구 삭제하기")
    private let lblNotice = UILabel().then {
        $0.text = "000-0000-0000\n\n\n이 전화번호로 인증번호를 보냅니다."
        $0.font = .systemFont(ofSize: 16)
        $0.textAlignment = .center
        $0.numberOfLines = 0
    }
    
    private let btnCancle = UIButton().then {
        $0.setTitle("취소", for: .normal)
        $0.backgroundColor = .systemBlue
    }
    
    let btnConfirm = UIButton().then {
        $0.setTitle("확인", for: .normal)
        $0.backgroundColor = .systemBlue
    }
    
    // MARK: - Properties
    let bag = DisposeBag()
    private let _bag = DisposeBag()
    
    
    // MARK: - Init
    override init(frame: CGRect) {
        super.init(frame: .zero)
        setUI()
        bindRx()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    // MARK: - Helper
    func setUI() {
        [lblNotice, btnCancle, btnConfirm].forEach {
            viewModal.addSubview($0)
        }
        
        [viewBackground, viewModal].forEach {
            self.addSubview($0)
        }
        
        viewBackground.snp.makeConstraints {
            $0.edges.equalToSuperview()
        }
        
        lblNotice.snp.makeConstraints {
            $0.top.leading.equalToSuperview().offset(20)
            $0.trailing.equalToSuperview().inset(20)
            $0.bottom.equalToSuperview().inset(80)
        }
        
        btnConfirm.snp.makeConstraints {
            $0.bottom.trailing.equalToSuperview().inset(20)
            $0.height.equalTo(40)
            $0.width.equalTo(60)
        }
        
        btnCancle.snp.makeConstraints {
            $0.trailing.equalTo(btnConfirm.snp.leading).offset(-10)
            $0.bottom.height.width.equalTo(btnConfirm)
        }
        
        viewModal.snp.makeConstraints {
            $0.center.equalToSuperview()
            $0.leading.equalToSuperview().offset(20)
            $0.trailing.equalToSuperview().inset(20)
        }
    }
}


// MARK: - Bind
extension ConfirmModalView {
    func bindRx() {
        bindButton()
    }
    
    func bindButton() {
        btnCancle.rx.tap
            .subscribe(onNext: { [weak self] _ in
                guard let self = self else { return }
                self.isHidden = true
            }).disposed(by: _bag)
    }
}
