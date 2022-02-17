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
import RxCocoa
import IQKeyboardManagerSwift
import RxGesture

class ChatRoomViewController: UIViewController {
    
    // MARK: - UI Properties
    /// MessageCollectionView
    private var collectionView: UICollectionView!
    
    private let bottomView = UIView().then {
        $0.backgroundColor = .white
    }
    
    /// 텍스트필드 배경 뷰
    private let textFieldView = UIView().then {
        $0.backgroundColor = UIColor(red: 248/255, green: 248/255, blue: 248/255, alpha: 1)
        $0.layer.borderColor = UIColor(red: 222/255, green: 222/255, blue: 222/255, alpha: 1).cgColor
        $0.layer.borderWidth = 1
        $0.layer.cornerRadius = 18
        $0.clipsToBounds = true
    }
    
    /// 텍스트필드
    private let textField = UITextField().then {
        $0.font = .systemFont(ofSize: 15)
        $0.autocorrectionType = .no
        $0.inputAccessoryView = UIView()
    }
    
    /// 전송 버튼
    private let uiViewSend = UIView()
    
    private let ivSend = UIImageView().then {
        $0.image = UIImage(named: "send")
    }
    
    /// 플러스 버튼
    private let ivMedia = UIImageView().then {
        $0.image = UIImage(named: "media")
        $0.isUserInteractionEnabled = false
    }
    
    /// 리프레쉬 UI
    private let refreshControl = UIRefreshControl()
    
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
        
        textField.delegate = self
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillShow(_:)), name: UIResponder.keyboardWillShowNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillHide(_:)), name: UIResponder.keyboardWillHideNotification, object: nil)
        
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
        IQKeyboardManager.shared.enable = false
        IQKeyboardManager.shared.shouldResignOnTouchOutside = false
        setNavigationAppearance()
        connectSocket()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        IQKeyboardManager.shared.enable = true
        IQKeyboardManager.shared.shouldResignOnTouchOutside = true
        resetNavigationAppearance()
        disconnectSocket()
    }
    
    // MARK: - Helper
    @objc
    private func keyboardWillShow(_ sender: Notification) {
        if let keyboardFrame: NSValue = sender.userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue {
            let height = keyboardFrame.cgRectValue.origin.y - textFieldView.frame.height - 10
            resetKeyboardPosition(height: height, isEditing: true)
        }
    }
    
    @objc
    private func keyboardWillHide(_ sender: Notification) {
        resetKeyboardPosition(isEditing: false)
    }
    
    // MARK: resetKeyboardPosition
    private func resetKeyboardPosition(height: CGFloat? = nil, isEditing: Bool) {
        // bottomView.frame.origin.y = height
        if isEditing {
            let height: CGFloat = height ?? view.frame.size.height - bottomView.frame.height
            bottomView.snp.remakeConstraints {
                $0.top.equalTo(height)
                $0.bottom.equalToSuperview()
                $0.leading.trailing.equalToSuperview()
            }
        }
        else {
            textField.endEditing(!isEditing)
            bottomView.snp.remakeConstraints {
                $0.bottom.equalToSuperview()
                $0.top.equalTo(view.safeAreaLayoutGuide.snp.bottom).inset(50)
                $0.leading.trailing.equalToSuperview()
            }
        }

        collectionView.snp.remakeConstraints {
            $0.top.leading.trailing.equalToSuperview()
            $0.bottom.equalTo(bottomView.snp.top)
        }
        view.layoutIfNeeded()
    }
    
    // MARK: connectSocket
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
    
    // MARK: disconnectSocket
    private func disconnectSocket() {
        let appDelegate = UIApplication.shared.delegate as? AppDelegate
        appDelegate?.removeChatSocket()
        viewModel.dependency.socket.accept(nil)
    }
    
    // MARK: fetch (첫 메시지 불러오기)
    private func fetch() {
        viewModel.initailMessageFetch()
        viewModel.fetchRoomInfo()
    }
    
    // MARK: setBackButton
    private func setBackButton() {
        self.navigationController?.navigationBar.tintColor = .black
    }
    
    // MARK: resetNavigationAppearance
    private func resetNavigationAppearance() {
        navigationController?.isNavigationBarHidden = true
        navigationController?.tabBarController?.tabBar.isHidden = false
        navigationController?.navigationBar.standardAppearance = UINavigationBarAppearance()
        navigationController?.navigationBar.scrollEdgeAppearance = UINavigationBarAppearance()
    }
    
    // MARK: setNavigationAppearance
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
    
    // MARK: handleRefresh
    @objc func handleRefresh(refreshControl: UIRefreshControl) {
        if !viewModel.dependency.isLoading.value,
           !viewModel.dependency.hasFirstMessage.value {
            viewModel.fetchPreviousMessages()
        }
    }
    
}

// MARK: - BaseViewController
extension ChatRoomViewController {
    // MARK: configureView
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
        
        refreshControl.addTarget(self, action: #selector(handleRefresh(refreshControl:)), for: UIControl.Event.valueChanged)
        collectionView.addSubview(refreshControl)
        
        textFieldView.addSubview(textField)
        
        bottomView.addSubview(ivMedia)
        bottomView.addSubview(textFieldView)
        
        uiViewSend.addSubview(ivSend)
        bottomView.addSubview(uiViewSend)
        view.addSubview(bottomView)
    }
    
    // MARK: configureSubviews
    func configureSubviews() {
        ivSend.snp.makeConstraints {
            $0.top.equalTo(textFieldView).offset(4)
            $0.trailing.equalTo(textFieldView).inset(4)
            $0.width.height.equalTo(28)
        }
        
        uiViewSend.snp.makeConstraints {
            $0.top.equalTo(bottomView).offset(-10)
            $0.trailing.equalTo(view)
            $0.bottom.equalTo(textFieldView).offset(10)
            $0.leading.equalTo(textField.snp.trailing).inset(10)
        }
        
        textField.snp.makeConstraints {
            $0.top.bottom.equalToSuperview()
            $0.leading.equalToSuperview().offset(14)
            $0.trailing.equalTo(ivSend.snp.leading).inset(-10)
        }
        
        textFieldView.snp.makeConstraints {
            $0.leading.equalToSuperview().offset(50)
            $0.trailing.equalToSuperview().inset(10)
            $0.top.equalToSuperview().offset(4)
            $0.height.equalTo(36)
        }
        
        ivMedia.snp.makeConstraints {
            $0.leading.equalToSuperview().offset(14)
            $0.centerY.equalTo(textFieldView)
            $0.width.height.equalTo(26)
        }
        
        bottomView.snp.makeConstraints {
            $0.bottom.equalToSuperview()
            $0.top.equalTo(view.safeAreaLayoutGuide.snp.bottom).inset(50)
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
        bindCollectionView()
        bindButton()
    }
    
    // MARK: Bind ViewModel
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
            .subscribe(onNext: { [weak self] _ in
                guard let self = self else {
                    return
                }
                print("🚧 메시지 리스트 수신")
                self.collectionView.reloadData()
                self.collectionView.layoutIfNeeded()
                if self.viewModel.dependency.rawMessages.value.last?.userId == self.myId {
                    DispatchQueue.main.async {
                        self.collectionView.scrollToBottom(animated: false)
                    }
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
        
        viewModel.dependency.prevMessages
            .subscribe(onNext: { [weak self] prevMessages in
                self?.refreshControl.endRefreshing()
                guard let self = self,
                      prevMessages.count > 0 else {
                    return
                }
                let oldVal = self.viewModel.dependency.rawMessages.value
                var newVal = oldVal
                let count = prevMessages.count - 1
                for i in 0...count {
                    newVal.insert(prevMessages[count-i], at: 0)
                }
                self.viewModel.dependency.rawMessages.accept(newVal)
            }).disposed(by: bag)
    }
    
    // MARK: Bind CollectionView
    private func bindCollectionView() {
        collectionView.rx.tapGesture()
            .when(.ended)
            .subscribe(onNext: { [weak self] _ in
                guard let self = self else {
                    return
                }
                self.resetKeyboardPosition(isEditing: false)
            }).disposed(by: bag)
    }
    
    // MARK: Bind textfield
    private func bindTextField() {
        textField.rx.text
            .orEmpty
            .subscribe(onNext: { [weak self] text in
                guard let self = self else {
                    return
                }
                if text.count > 0 {
                    self.ivSend.isHidden = false
                } else {
                    self.ivSend.isHidden = true
                }
                self.viewModel.input.text.accept(text)
            }).disposed(by: bag)
    }
    
    // MARK: Bind button
    private func bindButton() {
        uiViewSend.rx.tapGesture()
            .when(.recognized)
            .subscribe(onNext: { [weak self] _ in
                guard let self = self else {
                    return
                }
                self.viewModel.sendMessage()
                self.textField.text = ""
                self.viewModel.input.text.accept("")
                self.ivSend.isHidden = true
            }).disposed(by: bag)
    }
    
    // MARK: Bind Socket
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


// MARK: - MessageCollectionViewLayoutDelegate
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

// MARK: - UITextFieldDelegate
extension ChatRoomViewController: UITextFieldDelegate {
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        self.view.endEditing(true)
    }
    
    func textFieldDidEndEditing(_ textField: UITextField) {
        print("hello")
    }
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        return false
    }
}
