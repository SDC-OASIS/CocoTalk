//
//  FriendListViewController.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/17.
//

import UIKit
import SnapKit
import Then
import RxSwift

/// 친구목록 뷰컨트롤러
class FriendListViewController: UIViewController {
    
    // MARK: - UI Properties
    /// 네비게이션 바
    private let gnbView = GNBView()
    
    /// 테이블 뷰
    private let tableView = UITableView()
    
    // MARK: - Properties
    var bag = DisposeBag()
    var viewModel = FriendListViewModel()
    
    // MARK: - Life cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .white
        
        navigationController?.isNavigationBarHidden = true
        gnbView.setDelegate(delegate: self)
        
        configureView()
        configureSubviews()
        bindRx()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        fetch()
    }
    
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
    }
    
    // MARK: - Helper
    private func openChatRoom() {
        tabBarController?.selectedIndex = 1
        let chatRoomListVC = tabBarController?.viewControllers![1] as! UINavigationController
        let members = viewModel.output.talkMembers.value ?? []
        let roomId = viewModel.output.roomId.value ?? ""
        let vc = ChatRoomViewController(members: members, roomId: roomId)
        
        let member = members.filter { $0.userId != UserRepository.myProfile?.id }.first
        vc.title = member?.username ?? ""
        
        vc.hidesBottomBarWhenPushed = true
        chatRoomListVC.pushViewController(vc, animated: true)
    }
    
    private func fetch() {
        viewModel.fetch()
    }
}

// MARK: - BaseViewController
extension FriendListViewController {
    func configureView() {
        tableView.separatorStyle = .none
        tableView.delegate = self
        tableView.dataSource = self
        tableView.register(ProfileTableViewCell.self, forCellReuseIdentifier: ProfileTableViewCell.identifier)
        tableView.register(MyProfileCell.self, forCellReuseIdentifier: MyProfileCell.identifier)
        tableView.rowHeight = UITableView.automaticDimension
        tableView.estimatedRowHeight = 60
        view.addSubview(tableView)
        view.addSubview(gnbView)
    }
    
    func configureSubviews() {
        gnbView.snp.makeConstraints {
            $0.top.leading.trailing.equalToSuperview()
            $0.height.equalTo(100)
        }
        
        tableView.snp.makeConstraints {
            $0.top.equalTo(gnbView.snp.bottom)
            $0.leading.trailing.bottom.equalTo(view.safeAreaLayoutGuide)
        }
    }
}

// MARK: - Bindable
extension FriendListViewController {
    func bindRx() {
        bindViewModel()
        bindSocket()
    }
    
    func bindViewModel() {
        viewModel.output.friends
            .subscribe(onNext: { [weak self] _ in
                guard let self = self else {
                    return
                }
                self.tableView.reloadData()
            }).disposed(by: bag)
        
        viewModel.output.myProfile
            .subscribe(onNext: { [weak self] _ in
                guard let self = self else {
                    return
                }
                self.tableView.reloadData()
            }).disposed(by: bag)
        
        viewModel.output.isRoomExist
            .subscribe(onNext: { [weak self] isRoomExist in
                guard let self = self else {
                    return
                }
                
                if let isRoomExist = isRoomExist,
                    isRoomExist {
                        self.openChatRoom()
                }
                
            }).disposed(by: bag)
    }
    
    func bindSocket() {
//        소켓 불러오기
//        createChatRequestLog
    }
}

// MARK: - UITableViewDelegate, UITableViewDatasource
extension FriendListViewController: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, estimatedHeightForRowAt indexPath: IndexPath) -> CGFloat {
        return UITableView.automaticDimension
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        switch section {
        case 0:
            return 1
        case 1:
            return viewModel.dependency.favoriteProfile.count
        default:
            return viewModel.output.friends.value.count
        }
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        switch indexPath.section {
        case 0:
            let cell = tableView.dequeueReusableCell(withIdentifier: MyProfileCell.identifier, for: indexPath) as! MyProfileCell
            cell.setData(data: viewModel.output.myProfile.value)
            return cell
        case 1:
            let cell = tableView.dequeueReusableCell(withIdentifier: ProfileTableViewCell.identifier, for: indexPath) as! ProfileTableViewCell
            cell.setData(data: viewModel.dependency.favoriteProfile[indexPath.row])
            return cell
        default:
            let profile = viewModel.output.friends.value[indexPath.row]
            let cell = tableView.dequeueReusableCell(withIdentifier: ProfileTableViewCell.identifier, for: indexPath) as! ProfileTableViewCell
            cell.setData(data: profile)
            return cell
        }
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return viewModel.dependency.sections.count
    }
    
    func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        return viewModel.dependency.sections[section]
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let profile: ModelProfile
        if indexPath.section == 0 {
            profile = viewModel.output.myProfile.value
        } else {
            profile = viewModel.output.friends.value[indexPath.row]
        }
        let profileVC = ProfileModalViewController(profile: profile)
        profileVC.modalPresentationStyle = .overFullScreen
        profileVC.modalTransitionStyle = .coverVertical
        profileVC.delegate = self
        self.present(profileVC, animated: true)
        tableView.deselectRow(at: indexPath, animated: true)
    }
    
}

// MARK: - PrfofileCellDelegate
extension FriendListViewController: ProfileCellDelegate {
    func checkChatRoomExist(userId: Int) {
        viewModel.checkChatRoomExist(userId: userId)
    }
}


// MARK: - GNBDelegate
extension FriendListViewController: GNBDelegate {
    func gnbTabType() -> TabEnum {
        return .friend
    }
    
    func tapAddFriend() {
        let addFriendVC = AddFriendViewController()
        addFriendVC.modalPresentationStyle = .overFullScreen
        addFriendVC.modalTransitionStyle = .coverVertical
        addFriendVC.delegate = self
        self.present(addFriendVC, animated: true)
    }
    
    func tapSearch() {
        print("search")
    }
    
    func tapSetting() {
        print("setting")
    }
}

// MARK: - AddFriendDelegate
extension FriendListViewController: AddFriendDelegate {
    func didAddFriend() {
        #warning("코어 데이터로 불러오기")
        viewModel.getFriends()
    }
}
