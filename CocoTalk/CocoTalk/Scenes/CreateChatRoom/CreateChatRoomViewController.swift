//
//  CreateChatRoomViewController.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/12.
//

import UIKit
import SnapKit
import Then
import RxSwift

class CreateChatRoomViewController: UIViewController {
    
    // MARK: - UI Properties
    /// close 버튼
    private let ivClose = UIImageView(image: UIImage(systemName: "xmark")!).then {
        $0.tintColor = .black
    }

    /// title 라벨
    private let lblTitle = UILabel().then {
        $0.text = "대화상대 선택"
        $0.font = .systemFont(ofSize: 20, weight: .semibold)
        $0.textColor = .black
    }

    /// 확인 stackView
    private let stackViewConfirm = UIStackView().then {
        $0.axis = .horizontal
        $0.spacing = 4
    }
    
    /// 학인
    private let lblConfirm = UILabel().then {
        $0.text = "확인"
        $0.font = .systemFont(ofSize: 18)
        $0.textColor = .black
    }
    
    /// 사람 수 라벨
    private let lblCount = UILabel().then {
        $0.font = .systemFont(ofSize: 18)
        $0.textColor = .systemGreen
    }
    
    /// 선택된 사람 collectionView
    private var collectionView: UICollectionView!
    
    /// 이름 텍스트 필드
    private let textFieldSearch = UITextField().then {
        $0.placeholder = "이름 검색"
        $0.textColor = .black
    }
    
    /// 친구 라벨
    private let lblFriend = UILabel().then {
        $0.text = "친구"
        $0.textColor = .secondaryLabel
        $0.font = .systemFont(ofSize: 14)
    }
    
    /// 친구 리스트 테이블 뷰
    private var tableView: UITableView!
    
    // MARK: - Properties
    let bag = DisposeBag()
    let viewModel = CreateChatRoomViewModel()
    var delegate: CreateChatRoomDelegate?
    
    // MARK: - Life cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .white
        navigationController?.isNavigationBarHidden = true
        
        title = "대화 상대 선택"
        
        configureView()
        configureSubviews()
        bind()
    }
    
    // MARK: - Helper
    private func showAlert(title: String, message: String) {
        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "확인", style: .default){ _ in
            self.dismiss(animated: true)
        })
        present(alert, animated: true)
    }
}

// MARK: - BaseViewController
extension CreateChatRoomViewController {
    func configureView() {
        let flowLayout = UICollectionViewFlowLayout()
        flowLayout.scrollDirection = .horizontal
        collectionView = UICollectionView(frame: .zero, collectionViewLayout: flowLayout).then {
            $0.showsHorizontalScrollIndicator = false
        }
        collectionView.register(ClosableFriendCell.self, forCellWithReuseIdentifier: ClosableFriendCell.identifier)
        collectionView.delegate = self
        collectionView.dataSource = self
        
        tableView = UITableView()
        tableView.register(SelectableFriendCell.self, forCellReuseIdentifier: SelectableFriendCell.identifier)
        tableView.delegate = self
        tableView.dataSource = self
        
        
        stackViewConfirm.addArrangedSubview(lblCount)
        stackViewConfirm.addArrangedSubview(lblConfirm)
        
        [lblTitle, ivClose, stackViewConfirm, collectionView, textFieldSearch, lblFriend, tableView].forEach {
            view.addSubview($0)
        }
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
        
        stackViewConfirm.snp.makeConstraints {
            $0.centerY.equalTo(lblTitle)
            $0.trailing.equalToSuperview().inset(30)
            $0.height.equalTo(20)
        }
        
        collectionView.snp.makeConstraints {
            $0.top.equalTo(lblTitle.snp.bottom).offset(4)
            $0.leading.trailing.equalToSuperview()
            $0.height.equalTo(80)
        }
        
        textFieldSearch.snp.makeConstraints {
            $0.top.equalTo(collectionView.snp.bottom).offset(10)
            $0.leading.equalToSuperview().offset(30)
            $0.trailing.equalToSuperview().inset(30)
            $0.height.equalTo(30)
        }
        
        lblFriend.snp.makeConstraints {
            $0.top.equalTo(textFieldSearch.snp.bottom).offset(16)
            $0.leading.equalToSuperview().offset(10)
        }
        
        tableView.snp.makeConstraints {
            $0.top.equalTo(lblFriend.snp.bottom).offset(10)
            $0.leading.trailing.bottom.equalToSuperview()
        }
    }
}

// MARK: - Bindable
extension CreateChatRoomViewController {
    func bind() {
        bindViewModel()
        bindTextField()
        bindButton()
    }
    
    func bindViewModel() {
        viewModel.dependency.selectableFriends
            .subscribe(onNext: { [weak self] _ in
                guard let self = self else {
                    return
                }
                let count = self.viewModel.dependency.selectedFriends.count
                self.lblCount.text = "\(count)"
                self.collectionView.reloadData()
            }).disposed(by: bag)
        
        viewModel.dependency.filteredSelectableFriends
            .subscribe(onNext: { [weak self] _ in
                guard let self = self else {
                    return
                }
                self.tableView.reloadData()
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
                       #warning("채팅방 열기")
                       self.delegate?.fetchChatRoom()
                   }
                    self.dismiss(animated: true)
                }
                
            }).disposed(by: bag)
    
        viewModel.output.isRoomExist
            .subscribe(onNext: { [weak self] isRoomExist in
                guard let self = self else {
                    return
                }
                
                guard let isRoomExist = isRoomExist,
                      isRoomExist else {
                    return
                }
                self.showAlert(title: "채팅방 생성", message: "이미 존재하는 채팅방입니다.\n개인톡을 확인해주세요!")
            }).disposed(by: bag)
    }
    
    func bindTextField() {
        textFieldSearch.rx.text
            .orEmpty
            .debounce(.milliseconds(500), scheduler: MainScheduler.instance)
            .subscribe(onNext: { [weak self] text in
                guard let self = self else {
                    return
                }
                self.viewModel.input.keyword.accept(text)
                self.viewModel.findFriendByKeyword()
            }).disposed(by: bag)
    }
    
    func bindButton() {
        ivClose.rx.tapGesture()
            .when(.recognized)
            .subscribe(onNext: { [weak self] _ in
                guard let self = self else {
                    return
                }
                self.dismiss(animated: true)
            }).disposed(by: bag)
        
        lblConfirm.rx.tapGesture()
            .when(.recognized)
            .subscribe(onNext: { [weak self] _ in
                guard let self = self else {
                    return
                }
                let selected = self.viewModel.dependency.selectedFriends
                
                if selected.count == 1 {
                    self.viewModel.checkChatRoomExist()
                } else if selected.count > 1 {
                    let vc = CreateGroupChatViewController(members: selected)
                    vc.delegate = self.delegate
                    self.navigationController?.pushViewController(vc, animated: true)
                }
            }).disposed(by: bag)
    }
}

// MARK:
extension CreateChatRoomViewController: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return viewModel.dependency.filteredSelectableFriends.value.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: SelectableFriendCell.identifier, for: indexPath) as! SelectableFriendCell
        cell.setData(data: viewModel.dependency.filteredSelectableFriends.value[indexPath.row])
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        viewModel.selectFriend(indexPath.row)
        tableView.deselectRow(at: indexPath, animated: true)
    }
}

// MARK: - UICollectionViewDelegate, UICollectionViewDataSource
extension CreateChatRoomViewController: UICollectionViewDelegate, UICollectionViewDataSource {
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return viewModel.dependency.selectedFriends.count
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: ClosableFriendCell.identifier, for: indexPath) as! ClosableFriendCell
        cell.setData(data: viewModel.dependency.selectedFriends[indexPath.row])
        return cell
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        viewModel.deselectFriend(indexPath.row)
        collectionView.deselectItem(at: indexPath, animated: true)
    }
    
}
