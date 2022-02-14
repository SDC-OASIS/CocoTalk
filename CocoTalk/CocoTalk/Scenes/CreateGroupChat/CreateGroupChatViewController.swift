//
//  CreateGroupChatViewController.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/13.
//

import UIKit
import SnapKit
import Then
import RxSwift
import RxCocoa

class CreateGroupChatViewController: UIViewController {
    
    // MARK: - UI Properties
    /// 뒤로가기 버튼
    private let ivBack = UIImageView().then {
        $0.image = UIImage(systemName: "chevron.left")
        $0.tintColor = .black
    }
    
    /// 타이틀 라벨
    private let lblTitle = UILabel().then {
        $0.text = "그룹채팅방 정보 설정"
        $0.font = .systemFont(ofSize: 20, weight: .semibold)
        $0.textColor = .black
    }
    
    /// 확인 버튼
    private let lblConfirm = UILabel().then {
        $0.text = "확인"
        $0.font = .systemFont(ofSize: 18)
        $0.textColor = .black
    }
    
    /// 프로필 이미지
    #warning("다중 이미지로 수정")
    private let ivProfile = ProfileImageView(image: UIImage(named: "profile_noimg_thumbnail_01"))
    
    /// 카메라 이미지
#warning("카메라 이미지 추가")
    
    /// 텍스트 필드
    /// placeholder = 상대방이름
#warning("placeholder 재설정 ")
    private let textFieldChatTitle = UITextField().then {
        $0.placeholder = "채팅방 이름을 입력해주세요"
        $0.font = .systemFont(ofSize: 18)
    }
    
    /// 안내 라벨
    private let lblNotice = UILabel().then {
        $0.text = "채팅시작 전, 내가 설정한 그룹채팅방의 사진과 이름은 다른 모든 대화상대에게도 동일하게 보입니다. 채팅시작 후 설정한 사진과 이름은 나에게만 보입니다."
        $0.font = .systemFont(ofSize: 14)
        $0.numberOfLines = 0
        $0.textColor = .secondaryLabel
    }
    
    // MARK: - Properties
    private let viewModel = CreateGroupChatViewModel()
    private let bag = DisposeBag()
    var chatMembers: [SelectableProfile] = []
    var delegate: CreateChatRoomDelegate?
    
    // MARK: - Life cycle
    init(members: [SelectableProfile]) {
        chatMembers = members
        super.init(nibName: nil, bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .white
        navigationController?.isNavigationBarHidden = true
        
        setViewModel()
        configureView()
        configureSubviews()
        bind()
    }
    
    // MARK: - Helper
    private func setViewModel() {
        viewModel.dependency.selected.accept(chatMembers)
    }
    
    private func showAlert(title: String, message: String) {
        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "확인", style: .default){ _ in
            self.navigationController?.popViewController(animated: true)
        })
        present(alert, animated: true)
    }
}

// MARK: - BaseViewController
extension CreateGroupChatViewController {
    func configureView() {
        [ivBack, lblTitle, lblConfirm, ivProfile, textFieldChatTitle, lblNotice].forEach {
            view.addSubview($0)
        }
    }
    
    func configureSubviews() {
        lblTitle.snp.makeConstraints {
            $0.top.centerX.equalTo(view.safeAreaLayoutGuide)
        }
        
        ivBack.snp.makeConstraints {
            $0.centerY.equalTo(lblTitle)
            $0.leading.equalToSuperview().offset(30)
            $0.width.equalTo(18)
            $0.height.equalTo(20)
        }
        
        lblConfirm.snp.makeConstraints {
            $0.centerY.equalTo(lblTitle)
            $0.trailing.equalToSuperview().inset(30)
            $0.height.equalTo(20)
        }
        
        ivProfile.snp.makeConstraints {
            $0.top.equalTo(lblTitle.snp.bottom).offset(50)
            $0.centerX.equalToSuperview()
        }
        
        textFieldChatTitle.snp.makeConstraints {
            $0.top.equalTo(ivProfile.snp.bottom).offset(50)
            $0.leading.equalToSuperview().offset(20)
            $0.trailing.equalToSuperview().inset(20)
            $0.height.equalTo(40)
        }
        
        lblNotice.snp.makeConstraints {
            $0.top.equalTo(textFieldChatTitle.snp.bottom).offset(20)
            $0.leading.trailing.equalTo(textFieldChatTitle)
        }
    }
}

// MARK: - Bindable
extension CreateGroupChatViewController {
    func bind() {
        bindViewModel()
        bindTextField()
        bindButton()
    }
    
    func bindViewModel() {
        viewModel.dependency.selected
            .subscribe(onNext: { [weak self] _ in
                guard let self = self else {
                    return
                }
                self.textFieldChatTitle.placeholder = self.viewModel.dependency.defaultRoomName
            }).disposed(by: bag)
        
        viewModel.dependency.isFailed
            .subscribe(onNext: { [weak self] isFailed in
                guard let self = self else {
                    return
                }
                
                if let isFailed = isFailed {
                   if isFailed {
                       self.showAlert(title: "채팅방 생성 오류", message: "생성에 실패했습니다. 다시 시도해주세요.")
                   } else {
                       self.delegate?.fetchChatRoom()
                       self.dismiss(animated: true)
                   }
                }
                
            }).disposed(by: bag)
    }
    
    func bindTextField() {
        textFieldChatTitle.rx.text
            .orEmpty
            .subscribe(onNext: { [weak self] text in
                guard let self = self else {
                    return
                }
                self.viewModel.input.roomName.accept(text)
            }).disposed(by: bag)
    }
    
    func bindButton() {
        ivBack.rx.tapGesture()
            .when(.recognized)
            .subscribe(onNext: { [weak self] _ in
                guard let self = self else {
                    return
                }
                self.navigationController?.popViewController(animated: true)
            }).disposed(by: bag)
        
        lblConfirm.rx.tapGesture()
            .when(.recognized)
            .subscribe(onNext: { [weak self] _ in
                guard let self = self,
                      !self.viewModel.dependency.isLoading.value else {
                    return
                }
                self.viewModel.createChatRoom()
            }).disposed(by: bag)
    }
}
