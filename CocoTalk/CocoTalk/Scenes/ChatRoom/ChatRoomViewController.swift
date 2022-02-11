//
//  ChatRoomViewController.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/19.
//


import UIKit
import SnapKit
import Then
import RxSwift

class ChatRoomViewController: UIViewController {
    
    // MARK: - UI Properties
    /// MessageCollectionView
    private var collectionView: UICollectionView!
    
    private let textFieldView = ChatRoomTextFieldView().then {
        $0.backgroundColor = .gray
    }
    
    // MARK: - Properties
    let bag = DisposeBag()
    let viewModel = ChatRoomViewModel()
    
    var lineNumber = 0
    
    // MARK: - Life cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = UIColor(red: 171/255, green: 194/255, blue: 209/255, alpha: 1)
        
        #warning("타이틀 수정")
        title = "채팅방"
        
        configureView()
        configureSubviews()
        bind()
        fetch()
        
        #warning("눈 내리기 이펙트")
        // http://minsone.github.io/mac/ios/falling-snow-with-spritekit-on-uiview-in-swift
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        navigationController?.tabBarController?.tabBar.isHidden = true
        
        #warning("커스텀 네비게이션 바 구현")
        #warning("타이틀 + (사람수)")
        // https://dushyant37.medium.com/swift-4-recipe-using-attributed-string-in-navigation-bar-title-39f08f5cdb81
        let navAppearance = UINavigationBarAppearance()
        navAppearance.configureWithOpaqueBackground()
        navAppearance.shadowColor = .clear
        navAppearance.backgroundColor = UIColor(red: 171/255, green: 194/255, blue: 209/255, alpha: 0.9)
        navAppearance.titleTextAttributes = [.font: UIFont.systemFont(ofSize: 19, weight: .semibold)]
        navigationController?.navigationBar.standardAppearance = navAppearance
        navigationController?.navigationBar.scrollEdgeAppearance = navAppearance
    }
    
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        collectionView.scrollToBottom(animated: false)
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        navigationController?.tabBarController?.tabBar.isHidden = false
        navigationController?.navigationBar.standardAppearance = UINavigationBarAppearance()
        navigationController?.navigationBar.scrollEdgeAppearance = UINavigationBarAppearance()
    }
    
    // MARK: - Helper
    private func fetch() {
        viewModel.getMessages()
    }
}

// MARK: - BaseViewController
extension ChatRoomViewController {
    func configureView() {
        let flowLayout = MessageCollectionViewLayout()
        flowLayout.delegate = self
        collectionView = UICollectionView(frame: .zero, collectionViewLayout: flowLayout)
        collectionView.contentInset = UIEdgeInsets(top: 20, left: 0, bottom: 10, right: 0);
        collectionView.backgroundColor = .clear
        collectionView.register(MessageCollectionViewCell.self, forCellWithReuseIdentifier: MessageCollectionViewCell.identifier)
        collectionView.delegate = self
        collectionView.dataSource = self
        
        view.addSubview(collectionView)
        view.addSubview(textFieldView)
    }
    
    func configureSubviews() {
        textFieldView.snp.makeConstraints {
            $0.top.equalTo(view.safeAreaLayoutGuide.snp.bottom).inset(50)
            $0.height.equalTo(250)
            $0.leading.trailing.equalToSuperview()
        }
        
        collectionView.snp.makeConstraints {
            $0.top.leading.trailing.equalToSuperview()
            $0.bottom.equalTo(textFieldView.snp.top)
        }
    }
}

// MARK: - Bindable
extension ChatRoomViewController {
    func bind() {
        bindViewMdoel()
    }
    
    func bindViewMdoel() {
    }
}

// MARK: -  UICollectionViewDelegate, UICollectionViewDatasource
extension ChatRoomViewController: UICollectionViewDelegate, UICollectionViewDataSource {
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        viewModel.input.messages.count
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: MessageCollectionViewCell.identifier, for: indexPath) as! MessageCollectionViewCell
        cell.setData(data: viewModel.input.messages[indexPath.row])
        return cell
    }
}


extension ChatRoomViewController: MessageCollectionViewLayoutDelegate {
    func collectionView(_ collectionView: UICollectionView, heightForCellAtIndexPath indexPath: IndexPath) -> CGFloat {
        let width = collectionView.bounds.width
        let estimatedHeight: CGFloat = 800.0
        let dummyCell = MessageCollectionViewCell(frame: CGRect(x: 0, y: 0, width: width, height: estimatedHeight))
        dummyCell.setData(data: viewModel.input.messages[indexPath.row])
        dummyCell.layoutIfNeeded()
        let estimateSize = dummyCell.systemLayoutSizeFitting(CGSize(width: width, height: estimatedHeight))
        return estimateSize.height
    }
}
