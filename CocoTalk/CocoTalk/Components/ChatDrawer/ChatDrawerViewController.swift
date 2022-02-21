//
//  ChatDrawerViewController.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/19.
//

import UIKit
import SnapKit
import Then
import RxSwift
import RxCocoa

class ChatDrawerViewController: UIViewController {
    
    // MARK: - UI Properties
    private let uiview = UIView().then {
        $0.backgroundColor = UIColor(red: 0, green: 0, blue: 0, alpha: 0.15)
    }
    
    private let lblTitle = UILabel().then {
        $0.text = "채팅방 서랍"
        $0.font = .systemFont(ofSize: 16)
    }
    
    private let uiviewContentView = UIView().then {
        $0.backgroundColor = .white
    }
    
    private let lblMember = UILabel().then {
        $0.text = "대화 상대"
        $0.font = .systemFont(ofSize: 16)
    }
    
    private var tableViewProfile: UITableView!
    
    private let viewBottom = UIView().then {
        $0.backgroundColor = UIColor(red: 248/255, green: 248/255, blue: 248/255, alpha: 1)
        $0.layer.zPosition = 999
    }
    
    private let uiViewInvite = UIView()
    
    private let ivPlusButton = UIImageView().then {
        $0.image = UIImage(systemName: "plus.circle")
        $0.tintColor = .systemBlue
    }
    
    private let lblInvite = UILabel().then {
        $0.text = "대화상대 초대"
        $0.font = .systemFont(ofSize: 14)
        $0.textColor = .systemBlue
    }
    
    private let uiViewExit = UIView()
    
    private let ivExit = UIImageView().then {
        $0.image = UIImage(systemName: "arrow.left.square")
        $0.tintColor = .black
    }
    
    private let lblExit = UILabel().then {
        $0.text = "나가기"
        $0.font = .systemFont(ofSize: 14)
        $0.textColor = .black
    }
    
    // MARK: - Properties
    let bag = DisposeBag()
    let viewModel: ChatDrawerViewModel
    var roomMembers: [RoomMember]
    let roomId: String
    
    // MARK: - Life cycle
    init(roomId: String, members: [RoomMember]) {
        self.roomId = roomId
        self.roomMembers = members
        viewModel = ChatDrawerViewModel(roomId: roomId, roomMember: members)
        super.init(nibName: nil, bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .clear
        
        configureView()
        configureSubviews()
        bind()
    }
    
    // MARK: - Helper
}

// MARK: - BaseViewController
extension ChatDrawerViewController {
    func configureView() {
        tableViewProfile = UITableView()
        tableViewProfile.register(ProfileTableViewCell.self, forCellReuseIdentifier: ProfileTableViewCell.identifier)
        tableViewProfile.backgroundColor = .white
        tableViewProfile.delegate = self
        tableViewProfile.dataSource = self
        
        uiViewInvite.addSubview(ivPlusButton)
        uiViewInvite.addSubview(lblInvite)
        
        uiViewExit.addSubview(ivExit)
        uiViewExit.addSubview(lblExit)
        viewBottom.addSubview(uiViewExit)
        
        uiviewContentView.addSubview(viewBottom)
        uiviewContentView.addSubview(lblTitle)
        uiviewContentView.addSubview(lblMember)
        uiviewContentView.addSubview(tableViewProfile)
        uiviewContentView.addSubview(uiViewInvite)
        
        view.addSubview(uiview)
        view.addSubview(uiviewContentView)
    }

    func configureSubviews() {
        uiview.snp.makeConstraints {
            $0.edges.equalToSuperview()
        }
        
        uiViewExit.snp.makeConstraints {
            $0.top.equalTo(viewBottom).offset(24)
            $0.leading.equalTo(viewBottom).offset(12)
        }
        
        ivExit.snp.makeConstraints {
            $0.leading.centerY.equalToSuperview()
            $0.width.height.equalTo(26)
        }
        
        lblExit.snp.makeConstraints {
            $0.leading.equalTo(ivExit.snp.trailing).offset(6)
            $0.centerY.equalToSuperview()
        }
        
        uiViewInvite.snp.makeConstraints {
            $0.top.equalTo(lblMember.snp.bottom)
            $0.leading.trailing.equalToSuperview()
            $0.height.equalTo(60)
        }
        
        ivPlusButton.snp.makeConstraints {
            $0.leading.equalToSuperview().offset(24)
            $0.height.width.equalTo(30)
            $0.centerY.equalToSuperview()
        }
        
        lblInvite.snp.makeConstraints {
            $0.leading.equalTo(ivPlusButton.snp.trailing).offset(6)
            $0.trailing.top.bottom.equalToSuperview()
        }
        
        viewBottom.snp.makeConstraints {
            $0.bottom.trailing.equalToSuperview()
            $0.width.equalToSuperview()
            $0.top.equalTo(view.safeAreaLayoutGuide.snp.bottom).inset(50)
        }
        
        uiviewContentView.snp.makeConstraints {
            $0.top.equalTo(view.safeAreaLayoutGuide)
            $0.trailing.height.equalToSuperview()
            $0.width.equalToSuperview().multipliedBy(0.8)
            $0.bottom.equalTo(viewBottom.snp.top)
        }
        
        lblTitle.snp.makeConstraints {
            $0.top.leading.equalToSuperview().offset(10)
            $0.height.equalTo(30)
        }
        
        lblMember.snp.makeConstraints {
            $0.leading.height.equalTo(lblTitle)
            $0.top.equalTo(lblTitle.snp.bottom)
            $0.height.equalTo(30)
        }
        
        tableViewProfile.snp.makeConstraints {
            $0.top.equalTo(uiViewInvite.snp.bottom)
            $0.leading.trailing.equalToSuperview()
            $0.bottom.equalToSuperview()
        }
    }
}

// MARK: - Bindable
extension ChatDrawerViewController {
    func bind() {
        bindViewModel()
        bindButton()
    }
    
    private func bindViewModel() {
        
    }
    
    private func bindButton() {
        uiview.rx.tapGesture()
            .when(.ended)
            .subscribe(onNext: { [weak self] _ in
                guard let self = self else {
                    return
                }
                self.dismiss(animated: true)
            }).disposed(by: bag)
        
        uiViewInvite.rx.tapGesture()
            .when(.ended)
            .subscribe(onNext: { [weak self] _ in
                guard let _ = self else {
                    return
                }
                print("초대 클릭")
            }).disposed(by: bag)
    }
}

// MARK: - UITableViewDelegate, UITableViewDataSource
extension ChatDrawerViewController: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return viewModel.output.roomMember.value.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: ProfileTableViewCell.identifier, for: indexPath) as! ProfileTableViewCell
        
        let member = viewModel.output.roomMember.value[indexPath.row]
        if let profile = member.profile {
            let profileData = try? JSONDecoder().decode(ModelProfileData.self, from: Data(profile.utf8))
            cell.setData(data: ModelProfile(username: member.username,
                                            bio: profileData?.message,
                                            profileImageURL: profileData?.profile,
                                            bgImageURL: profileData?.background))
        }
        return cell
    }
    
    
}
