//
//  AddFriendViewController.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/12.
//

import UIKit
import SnapKit
import Then
import RxSwift
import RxCocoa
import Kingfisher

/// 친구추가 뷰컨트롤러
class AddFriendViewController: UIViewController {
    
    // MARK: - UI Properties
    private let lblTitle = UILabel().then {
        $0.text = "코코톡 ID로 추가"
        $0.font = .systemFont(ofSize: 20, weight: .semibold)
        $0.textColor = .black
    }
    
    private let ivClose = UIImageView(image: UIImage(systemName: "xmark")!).then {
        $0.tintColor = .black
    }
    
    private let lblNoResult = UILabel().then {
        $0.text = "검색 결과가 없습니다."
        $0.textColor = .secondaryLabel
    }
    
    /// 이메일 텍스트 필드
    private let textFieldCid = UITextField().then {
        $0.placeholder = "코코톡 아이디"
        $0.autocorrectionType = .no
        $0.autocapitalizationType = .none
        $0.spellCheckingType = .no
        $0.returnKeyType = .search
    }
    
    /// 내 아이디 뷰
    private let myCidView = UIView().then {
        $0.backgroundColor = .secondarySystemBackground
    }
    
    /// 내 아이디 라벨
    private let lblMyCid = UILabel().then {
        $0.text = "내 아이디"
        $0.font = .systemFont(ofSize: 14)
        $0.textColor = .secondaryLabel
    }
    
    /// 내 아이디 라벨
    private let lblMyCidValue = UILabel().then {
        $0.font = .systemFont(ofSize: 14)
        $0.textColor = .secondaryLabel
    }
    
    /// 프로필 뷰
    private let profileView = UIView().then {
        $0.backgroundColor = .secondarySystemBackground
    }
    
    /// 프로필 이미지
    private let ivProfile = ProfileImageView(image: UIImage(named: "profile_noimg_thumbnail_01")!)
    
    /// 프로필 이름
    private let lblName = UILabel().then {
        $0.font = .systemFont(ofSize: 18)
        $0.textColor = .black
    }
    
    /// 친구 추가 버튼
    private let btnAddFriend = PaddingLabel().then {
        $0.text = "친구 추가"
        $0.textColor = .white
        $0.backgroundColor = .systemGreen
        $0.layer.cornerRadius = 10
        $0.clipsToBounds = true
    }
    
    
    // MARK: - Properties
    let bag = DisposeBag()
    let viewModel = AddFriendViewModel()
    var delegate: AddFriendDelegate?
    
    // MARK: - Life cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .white
        
        configureView()
        configureSubviews()
        bind()
    }
    
    // MARK: - Helper
}

// MARK: - BaseViewController
extension AddFriendViewController {
    func configureView() {
        view.addSubview(lblTitle)
        view.addSubview(ivClose)
        view.addSubview(textFieldCid)
        view.addSubview(lblNoResult)
        
        myCidView.addSubview(lblMyCid)
        myCidView.addSubview(lblMyCidValue)
        view.addSubview(myCidView)
        
        profileView.addSubview(ivProfile)
        profileView.addSubview(lblName)
        profileView.addSubview(btnAddFriend)
        view.addSubview(profileView)
    }
    
    func configureSubviews() {
        lblTitle.snp.makeConstraints {
            $0.top.centerX.equalTo(view.safeAreaLayoutGuide)
        }
        
        ivClose.snp.makeConstraints {
            $0.centerY.equalTo(lblTitle)
            $0.leading.equalToSuperview().offset(30)
            $0.width.equalTo(18)
            $0.height.equalTo(20)
        }
        
        lblNoResult.snp.makeConstraints {
            $0.center.equalToSuperview()
        }
        lblNoResult.isHidden = true
        
        textFieldCid.snp.makeConstraints {
            $0.top.equalTo(view.safeAreaLayoutGuide).offset(100)
            $0.leading.equalToSuperview().offset(20)
            $0.trailing.equalToSuperview().inset(20)
        }
        
        myCidView.snp.makeConstraints {
            $0.top.equalTo(textFieldCid.snp.bottom).offset(20)
            $0.leading.trailing.equalTo(textFieldCid)
            $0.height.equalTo(40)
        }
        
        lblMyCid.snp.makeConstraints {
            $0.centerY.equalToSuperview()
            $0.leading.equalToSuperview().offset(10)
        }
        
        lblMyCidValue.snp.makeConstraints {
            $0.centerY.equalToSuperview()
            $0.trailing.equalToSuperview().inset(10)
        }
        
        profileView.snp.makeConstraints {
            $0.top.equalTo(textFieldCid.snp.bottom).offset(20)
            $0.leading.trailing.equalTo(textFieldCid)
            $0.height.equalTo(250)
        }
        
        ivProfile.snp.makeConstraints {
            $0.centerX.equalToSuperview()
            $0.top.equalToSuperview().offset(30)
            $0.height.width.equalTo(100)
        }

        lblName.snp.makeConstraints {
            $0.centerX.equalToSuperview()
            $0.top.equalTo(ivProfile.snp.bottom).offset(20)
        }
        
        btnAddFriend.snp.makeConstraints {
            $0.centerX.equalToSuperview()
            $0.top.equalTo(lblName.snp.bottom).offset(20)
        }
        profileView.isHidden = true
    }
}

// MARK: - Bindable
extension AddFriendViewController {
    func bind() {
        bindTextField()
        bindViewModel()
        bindButton()
    }
    
    private func bindTextField() {
        textFieldCid.rx.text
            .orEmpty
            .subscribe(onNext: { [weak self] value in
                guard let self = self else {
                    return
                }
                self.viewModel.input.cid.accept(value)
            }).disposed(by: bag)
        
        textFieldCid.rx.controlEvent([.editingDidEndOnExit])
            .subscribe(onNext: { [weak self] _ in
                guard let self = self else {
                    return
                }
                self.viewModel.findUserByCid()
            }).disposed(by: bag)

    }
    
    private func bindViewModel() {
        viewModel.dependency.myCid
            .subscribe(onNext: { [weak self] cid in
                guard let self = self,
                      let cid = cid else {
                    return
                }
                self.lblMyCidValue.text = cid
            }).disposed(by: bag)
        
        viewModel.dependency.noResult
            .subscribe(onNext: { [weak self] noResult in
                guard let self = self,
                      let noResult = noResult else {
                    return
                }
                if noResult {
                    self.myCidView.isHidden = true
                    self.profileView.isHidden = true
                    self.lblNoResult.isHidden = false
                } else {
                    self.lblNoResult.isHidden = true
                }
            }).disposed(by: bag)
        
        viewModel.output.resultProfile
            .subscribe(onNext: { [weak self] result in
                guard let self = self,
                      let result = result else {
                          self?.profileView.isHidden = true
                    return
                }
                if let imageURL = URL(string: result.profileImageURL ?? "") {
                    self.ivProfile.kf.setImage(with: imageURL)
                }
                self.lblName.text = result.username ?? ""
                self.myCidView.isHidden = true
                self.profileView.isHidden = false
            }).disposed(by: bag)
        
        viewModel.dependency.isAddCompleted
            .subscribe(onNext: { [weak self] isAddCompleted in
                guard let self = self,
                      let isAddCompleted = isAddCompleted,
                      isAddCompleted else {
                    return
                }
                self.delegate?.didAddFriend()
                self.dismiss(animated: true)
            }).disposed(by: bag)
    }
    
    private func bindButton() {
        ivClose.rx.tapGesture()
            .when(.recognized)
            .subscribe(onNext: { [weak self] _ in
                guard let self = self else {
                    return
                }
                self.dismiss(animated: true)
            }).disposed(by: bag)
        
        btnAddFriend.rx.tapGesture()
            .when(.recognized)
            .subscribe(onNext: { [weak self] _ in
                guard let self = self else {
                    return
                }
                if !self.viewModel.dependency.isLoading.value {
                    self.viewModel.addFriend()
                }
            }).disposed(by: bag)
    }
}
