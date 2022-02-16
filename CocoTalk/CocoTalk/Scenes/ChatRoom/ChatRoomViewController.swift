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
    
    private let bottomView = ChatRoomTextFieldView().then {
        $0.backgroundColor = .white
    }
    
    /// 텍스트필드 배경 뷰
    private let textFieldView = UIView().then {
        $0.backgroundColor = UIColor(red: 248/255, green: 248/255, blue: 248/255, alpha: 1)
        $0.layer.borderColor = UIColor(red: 222/255, green: 222/255, blue: 222/255, alpha: 1).cgColor
        $0.layer.borderWidth = 1
        $0.layer.cornerRadius = 20
        $0.clipsToBounds = true
    }
    
    /// 텍스트필드
    private let textField = UITextField().then {
        $0.autocorrectionType = .no
        $0.inputAccessoryView = UIView()
    }
    
    /// 전송 버튼
    private let ivSend = UIImageView().then {
        $0.image = UIImage(named: "send")
    }
    
    /// 플러스 버튼
    private let ivMedia = UIImageView().then {
        $0.image = UIImage(named: "media")
    }
    
    // MARK: - Properties
    let bag = DisposeBag()
    let viewModel: ChatRoomViewModel
    private var members: [RoomMember]
    private var roomId: String
    private var myId: Int?
    
    // MARK: - Life cycle
    
    init (members: [RoomMember], roomId: String) {
        self.members = members
        self.roomId = roomId
        self.viewModel = ChatRoomViewModel(roomId: roomId, members: members)
        
        if let savedData = UserDefaults.standard.object(forKey: UserDefaultsKey.myData.rawValue) as? Data,
           let myData = try? JSONDecoder().decode(ModelSignupResponse.self, from: savedData) {
            self.myId = myData.id
        }
        
        super.init(nibName: nil, bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = UIColor(red: 171/255, green: 194/255, blue: 209/255, alpha: 1)
        navigationController?.isNavigationBarHidden = false
        
        setBackButton()
        configureView()
        configureSubviews()
        bind()
        fetch()
        
        #warning("눈 내리기 이펙트")
        // http://minsone.github.io/mac/ios/falling-snow-with-spritekit-on-uiview-in-swift
    }
    
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        collectionView.scrollToBottom(animated: false)
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        setNavigationAppearance()
        connectSocket()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        resetNavigationAppearance()
        disconnectSocket()
    }
    
    // MARK: - Helper
    private func connectSocket() {
        var socket: WebSocketHelper
        if let dependecySocket = viewModel.dependency.socket.value {
            socket = dependecySocket
        } else {
            let appDelegate = UIApplication.shared.delegate as? AppDelegate
            if let appDelegateSocket = appDelegate?.chatSocket {
                socket = appDelegateSocket
            } else {
                socket = WebSocketHelper(socketType: .chatRoom, userId: self.myId,roomId: self.roomId)
                appDelegate?.chatSocket = socket
            }
        }
        socket.establishConnection()
        viewModel.dependency.socket.accept(socket)
        bindSocket()
    }
    
    private func disconnectSocket() {
        let appDelegate = UIApplication.shared.delegate as? AppDelegate
        appDelegate?.removeChatSocket()
        viewModel.dependency.socket.accept(nil)
    }
    
    private func fetch() {
        viewModel.getMessages()
        viewModel.fetchRoomInfo()
    }
    
    private func setBackButton() {
        self.navigationController?.navigationBar.tintColor = .black
    }
    
    private func resetNavigationAppearance() {
        navigationController?.isNavigationBarHidden = true
        navigationController?.tabBarController?.tabBar.isHidden = false
        navigationController?.navigationBar.standardAppearance = UINavigationBarAppearance()
        navigationController?.navigationBar.scrollEdgeAppearance = UINavigationBarAppearance()
    }
    
    private func setNavigationAppearance() {
        navigationController?.isNavigationBarHidden = false
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
        
        textFieldView.addSubview(textField)
        textFieldView.addSubview(ivSend)
        bottomView.addSubview(ivMedia)
        bottomView.addSubview(textFieldView)
        view.addSubview(bottomView)
    }
    
    func configureSubviews() {
        ivSend.snp.makeConstraints {
            $0.top.equalToSuperview().offset(4)
            $0.trailing.equalToSuperview().inset(4)
            $0.width.height.equalTo(32)
        }
        
        textField.snp.makeConstraints {
            $0.top.bottom.equalToSuperview()
            $0.leading.equalToSuperview().offset(14)
            $0.trailing.equalTo(ivSend.snp.leading).inset(-10)
        }
        
        textFieldView.snp.makeConstraints {
            $0.leading.equalToSuperview().offset(50)
            $0.trailing.equalToSuperview().inset(10)
            $0.top.equalToSuperview().offset(8)
            $0.bottom.lessThanOrEqualToSuperview()
            $0.height.equalTo(40)
        }
        
        ivMedia.snp.makeConstraints {
            $0.leading.equalToSuperview().offset(14)
            $0.centerY.equalTo(textFieldView)
            $0.width.height.equalTo(26)
        }
        
        bottomView.snp.makeConstraints {
            $0.bottom.equalToSuperview()
            $0.height.equalTo(70)
            $0.leading.trailing.equalToSuperview()
        }
        
        collectionView.snp.makeConstraints {
            $0.top.leading.trailing.equalToSuperview()
            $0.bottom.equalTo(bottomView.snp.top)
        }
    }
}

// MARK: - Bindable
extension ChatRoomViewController {
    func bind() {
        bindViewModel()
        bindTextField()
        bindButton()
    }
    
    // MARK: - BindViewModel
    private func bindViewModel() {
        viewModel.dependency.rawMessages
            .subscribe(onNext: { [weak self] rawMessages in
                guard let self = self else {
                    return
                }
                let processedMessages = self.viewModel.getProcessedMessages()
                self.viewModel.output.messages.accept(processedMessages)
            }).disposed(by: bag)
        
        viewModel.output.messages
            .subscribe(onNext: { [weak self] messages in
                guard let self = self else {
                    return
                }
                print("🚧 메시지 리스트 수신")
                self.collectionView.reloadData()
                self.collectionView.layoutIfNeeded()
                DispatchQueue.main.async {
                    self.collectionView.scrollToBottom(animated: false)
                }
            }).disposed(by: bag)
        
        viewModel.output.roomInfo
            .subscribe(onNext: { [weak self] roomInfo in
                guard let self = self else {
                    return
                }
                print("🚧 룸 정보 수정됨")
                let processedMessages = self.viewModel.getProcessedMessages()
                self.viewModel.output.messages.accept(processedMessages)
//                self.collectionView.reloadData()
//                self.collectionView.layoutIfNeeded()
//                DispatchQueue.main.async {
//                    self.collectionView.scrollToBottom(animated: false)
//                }
            }).disposed(by: bag)
        
        viewModel.output.members
            .subscribe(onNext: { [weak self] members in
                guard let self = self else {
                    return
                }
                var newValue = self.viewModel.dependency.userId2RoomMember.value
                members.forEach {
                    newValue[$0.userId ?? -1] = $0
                }
                self.viewModel.dependency.userId2RoomMember.accept(newValue)
            }).disposed(by: bag)
    }
    
    private func bindTextField() {
        textField.rx.text
            .orEmpty
            .subscribe(onNext: { [weak self] text in
                guard let self = self else {
                    return
                }
                self.viewModel.input.text.accept(text)
            }).disposed(by: bag)
    }
    
    private func bindButton() {
        ivSend.rx.tapGesture()
            .when(.recognized)
            .subscribe(onNext: { [weak self] _ in
                guard let self = self else {
                    return
                }
                self.viewModel.sendMessage()
                self.textField.text = ""
                self.viewModel.input.text.accept("")
            }).disposed(by: bag)
    }
    
    // MARK: - BindSocket
    private func bindSocket() {
        
        guard let socket = viewModel.dependency.socket.value else {
            return
        }
        
        socket.isSocketConnected
            .subscribe(onNext: { [weak self] isConnected in
                guard let self = self,
                      let isConnected = isConnected else {
                    return
                }
                
                if isConnected {
                    self.viewModel.fetchRoomInfo()
                }
            }).disposed(by: bag)
        
        socket.receivedUpdatedRoomInfo
            .subscribe(onNext: { [weak self] info in
                guard let self = self,
                      let info = info else {
                    return
                }
                self.viewModel.output.roomInfo.accept(info)
            }).disposed(by: bag)

        socket.receivedMessage
            .subscribe(onNext: { [weak self] receivedMessage in
                guard let self = self,
                      let receivedMessage = receivedMessage else {
                    return
                }
                let oldVal = self.viewModel.dependency.rawMessages.value
                var newVal = oldVal
                guard let newMessage = self.viewModel.convertMessage(receivedMessage) else {
                    return
                }
                newVal.append(newMessage)
                self.viewModel.dependency.rawMessages.accept(newVal)
            }).disposed(by: bag)
    }
}

// MARK: -  UICollectionViewDelegate, UICollectionViewDatasource
extension ChatRoomViewController: UICollectionViewDelegate, UICollectionViewDataSource {
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        viewModel.output.messages.value.count
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: MessageCollectionViewCell.identifier, for: indexPath) as! MessageCollectionViewCell
        cell.setData(data: viewModel.output.messages.value[indexPath.row])
        return cell
    }
}


extension ChatRoomViewController: MessageCollectionViewLayoutDelegate {
    func collectionView(_ collectionView: UICollectionView, heightForCellAtIndexPath indexPath: IndexPath) -> CGFloat {
        let width = collectionView.bounds.width
        let estimatedHeight: CGFloat = 800.0
        let dummyCell = MessageCollectionViewCell(frame: CGRect(x: 0, y: 0, width: width, height: estimatedHeight))
        dummyCell.setData(data: viewModel.output.messages.value[indexPath.row])
        dummyCell.layoutIfNeeded()
        let estimateSize = dummyCell.systemLayoutSizeFitting(CGSize(width: width, height: estimatedHeight))
        return estimateSize.height
    }
}
