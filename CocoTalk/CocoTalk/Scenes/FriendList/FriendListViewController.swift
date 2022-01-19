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

class FriendListViewController: UIViewController {
    
    // MARK: - UI Properties
    /// 테이블 뷰
    private let tableView = UITableView()
    
    // MARK: - Properties
    var bag = DisposeBag()
    var viewModel = FriendListViewModel()
    
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
#warning("Rx로 Delegate 설정")
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
    }
    
    func configureSubviews() {
        tableView.snp.makeConstraints {
            $0.edges.equalTo(view.safeAreaLayoutGuide)
        }
    }
}

// MARK: - Bindable
extension FriendListViewController {
    func bindRx() {}
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
            return viewModel.input.profileList.count
        }
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        switch indexPath.section {
        case 0:
            let cell = tableView.dequeueReusableCell(withIdentifier: MyProfileCell.identifier, for: indexPath) as! MyProfileCell
            cell.setData(data: viewModel.dependency.myProfile)
            return cell
        case 1:
            let cell = tableView.dequeueReusableCell(withIdentifier: ProfileTableViewCell.identifier, for: indexPath) as! ProfileTableViewCell
            cell.setData(data: viewModel.dependency.favoriteProfile[indexPath.row])
            return cell
        default:
            let profile = self.viewModel.input.profileList[indexPath.row]
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
        let profileVC = ProfileModalViewController()
        profileVC.modalPresentationStyle = .overFullScreen
        profileVC.modalTransitionStyle = .coverVertical
        profileVC.delegate = self
        self.present(profileVC, animated: true)
        tableView.deselectRow(at: indexPath, animated: true)
    }
    
}

// MARK: - PrfofileCellDelegate
extension FriendListViewController: ProfileCellDelegate {
    func openChatRoom() {
        tabBarController?.selectedIndex = 1
        let chatRoomListVC = tabBarController?.viewControllers![1] as! UINavigationController
        let vc = ChatRoomViewController()
        vc.hidesBottomBarWhenPushed = true
        chatRoomListVC.pushViewController(vc, animated: true)
    }
}
