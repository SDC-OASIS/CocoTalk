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
import PhotosUI
import AVKit

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
    
    /// 최근 메시지뷰
    private let uiviewNewMessage = UIView().then {
        $0.backgroundColor = .white
    }
    
    /// 리프레쉬 UI
    private let refreshControl = UIRefreshControl()
    
    /// 메뉴 버튼
    private let btnMenu = UIBarButtonItem().then {
        $0.image = UIImage(systemName: "line.3.horizontal")
    }
    
    // MARK: - Properties
    let bag = DisposeBag()
    let viewModel: ChatRoomViewModel
    private var members: [RoomMember]
    private var roomId: String
    private var myId: Int?
    private var keyboardHeight: CGFloat?
    private var isFirstMessageFetch: Bool = true
    
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
            keyboardHeight = height
            bottomView.snp.remakeConstraints {
                $0.top.equalTo(height)
                $0.bottom.equalToSuperview()
                $0.leading.trailing.equalToSuperview()
            }
            collectionView.snp.remakeConstraints {
                $0.top.leading.trailing.equalToSuperview()
                $0.bottom.equalTo(bottomView.snp.top)
            }
        }
    }
    
    @objc
    private func keyboardWillHide(_ sender: Notification) {
        shrinkBottomView()
    }
    
    // MARK: resetKeyboardPosition
    private func resetKeyboardPosition(isEditing: Bool) {
        if isEditing {
            expandBottomView()
        } else {
            textField.endEditing(!isEditing)
            shrinkBottomView()
        }
    }
    
    private func shrinkBottomView() {
        bottomView.snp.remakeConstraints {
            $0.bottom.equalToSuperview()
            $0.top.equalTo(view.safeAreaLayoutGuide.snp.bottom).inset(50)
            $0.leading.trailing.equalToSuperview()
        }
        collectionView.snp.remakeConstraints {
            $0.top.leading.trailing.equalToSuperview()
            $0.bottom.equalTo(bottomView.snp.top)
        }
    }
    
    private func expandBottomView() {
        let height = keyboardHeight ?? view.frame.size.height - 300
        bottomView.snp.remakeConstraints {
            $0.top.equalTo(height)
            $0.bottom.equalToSuperview()
            $0.leading.trailing.equalToSuperview()
        }
        collectionView.snp.remakeConstraints {
            $0.top.leading.trailing.equalToSuperview()
            $0.bottom.equalTo(bottomView.snp.top)
        }
    }
    
    private func showMediaActionSheet() {
        let alert = UIAlertController(title: "사진/영상 보내기", message: nil, preferredStyle: .actionSheet)
        
        let albumAction = UIAlertAction(title: "앨범에서 보내기", style: .default) { [weak self] alert in
            guard let self = self else {
                return
            }
            self.showAlbumView()
        }
        let cancelAction = UIAlertAction(title: "취소", style: .cancel)
        
        alert.addAction(albumAction)
        alert.addAction(cancelAction)
        
        present(alert, animated: true)
    }
    
    private func showAlbumView() {
        checkPermission()
    }
    
    private func checkPermission() {
        switch PHPhotoLibrary.authorizationStatus() {
        case .denied, .restricted:
            showPermissionAlert()
        case .authorized, .limited:
            presentPHPicker()
        case .notDetermined:
            PHPhotoLibrary.requestAuthorization { [weak self] status in
                guard let self = self else {
                    return
                }
                if status == .authorized {
                    DispatchQueue.main.async {
                        self.presentPHPicker()
                    }
                } else {
                    DispatchQueue.main.async {
                        self.showPermissionAlert()
                    }
                }
            }
        default:
            showPermissionAlert()
        }
    }
    
    private func showPermissionAlert() {
        let alert = UIAlertController(title: "앨범 권한 요청", message: "프로필 수정을 위해 사진첩 권한을 허용해야합니다.", preferredStyle: .alert)
        let disagree = UIAlertAction(title: "허용 안함", style: .default)
        let agree = UIAlertAction(title: "허용", style: .default) { alert in
            guard let settingsUrl = URL(string: UIApplication.openSettingsURLString) else {
                return
            }
            if UIApplication.shared.canOpenURL(settingsUrl) {
                UIApplication.shared.open(settingsUrl)
            }
        }
        alert.addAction(disagree)
        alert.addAction(agree)
        present(alert, animated: true)
    }
    
    private func presentPHPicker() {
        var configuration = PHPickerConfiguration()
        configuration.filter = .any(of: [.videos, .images])
        let phpicker = PHPickerViewController(configuration: configuration)
        phpicker.modalPresentationStyle = .overFullScreen
        phpicker.delegate = self
        self.present(phpicker, animated: true)
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
        navigationController?.navigationItem.rightBarButtonItem = nil
    }
    
    // MARK: setNavigationAppearance
    private func setNavigationAppearance() {
        navigationController?.isNavigationBarHidden = false
        navigationController?.tabBarController?.tabBar.isHidden = true
        
        // https://dushyant37.medium.com/swift-4-recipe-using-attributed-string-in-navigation-bar-title-39f08f5cdb81
        let navAppearance = UINavigationBarAppearance()
        navAppearance.configureWithOpaqueBackground()
        navAppearance.shadowColor = .clear
        navAppearance.backgroundColor = UIColor(red: 171/255, green: 194/255, blue: 209/255, alpha: 0.9)
        navAppearance.titleTextAttributes = [.font: UIFont.systemFont(ofSize: 19, weight: .semibold)]
        navigationController?.navigationBar.standardAppearance = navAppearance
        navigationController?.navigationBar.scrollEdgeAppearance = navAppearance
        self.navigationItem.rightBarButtonItem = btnMenu
    }
    
    // MARK: handleRefresh
    @objc func handleRefresh(refreshControl: UIRefreshControl) {
        if !viewModel.dependency.isRefreshing.value,
           !viewModel.dependency.isLoading.value,
           !viewModel.dependency.hasFirstMessage.value {
            viewModel.dependency.isRefreshing.accept(true)
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
            .subscribe(onNext: { [weak self] messages in
                guard let self = self else {
                    return
                }
                print("🚧 메시지 리스트 수신")
                self.collectionView.reloadData()
                self.collectionView.layoutIfNeeded()
                
                if self.viewModel.dependency.isRefreshing.value { // 이전 메시지를 불러올 경우 X
                    self.viewModel.dependency.isRefreshing.accept(false)
                    return
                } else if self.isFirstMessageFetch { // 첫번째 로딩일 경우
                    if messages.count > 0 {
                        self.isFirstMessageFetch = false
                    }
                    DispatchQueue.main.async {
                        self.collectionView.scrollToBottom(animated: false)
                    }
                } else if !self.isFirstMessageFetch &&
                            (self.viewModel.dependency.rawMessages.value.last?.userId == self.myId) { // 내가 보낸 메시지인가
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
        
        viewModel.dependency.uploadingMediaFileUUID
            .subscribe(onNext: { [weak self] uuid in
                guard let self = self,
                      let uuid = uuid else {
                    return
                }
                let oldVal = self.viewModel.dependency.rawMessages.value
                var newVal = oldVal
                let sentAt = Date()
                
                let dateFormatter = DateFormatter()
                dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS"
                let sentAtString = dateFormatter.string(from: sentAt)
                
                newVal.append(ModelMessage(id: uuid.description,
                                           roomId: self.roomId,
                                           messageBundleId: nil,
                                           userId: self.myId,
                                           content: "업로드 중...",
                                           type: 2,
                                           sentAt: sentAtString))
                self.viewModel.dependency.rawMessages.accept(newVal)
            }).disposed(by: bag)
        
        viewModel.dependency.successToSendMedia
            .subscribe(onNext: { [weak self] uuid in
                guard let self = self,
                      let uuid = uuid else {
                    return
                }
                let oldVal = self.viewModel.dependency.uploadingMediaFileUUIDList.value
                let newVal = oldVal.filter { $0 != uuid }
                self.viewModel.dependency.uploadingMediaFileUUIDList.accept(newVal)
                
                let oldMessages = self.viewModel.dependency.rawMessages.value
                let newMessages = oldMessages.filter { $0.id != uuid.description }
                self.viewModel.dependency.rawMessages.accept(newMessages)
            }).disposed(by: bag)
        
        viewModel.dependency.postedMediaFileURL
            .subscribe(onNext: { [weak self] response in
                guard let self = self,
                      let urlString = response,
                      let room = self.viewModel.output.roomInfo.value else {
                    return
                }
                
                let filename: NSString = urlString as NSString
                let pathExtension = filename.pathExtension
                print(pathExtension)
                
                let message: ModelPubChatMessage?
                if pathExtension == "mp4" {
                    message = self.viewModel.buildMessage(room, content: urlString, type: 5)
                } else {
                    message = self.viewModel.buildMessage(room, content: urlString, type: 4)
                }
                
                guard let message = message else {
                    return
                }
                
                self.viewModel.sendMedia(message)
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
                self.shrinkBottomView()
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
        
        ivMedia.rx.tapGesture()
            .when(.recognized)
            .subscribe(onNext: { [weak self] _ in
                guard let self = self else {
                    return
                }
                self.showMediaActionSheet()
            }).disposed(by: bag)
        
        btnMenu.rx.tap
            .subscribe(onNext: { [weak self] _ in
                guard let self = self else {
                    return
                }
                let vc = ChatDrawerViewController(roomId: self.roomId, members: self.members)
                vc.modalPresentationStyle = .overFullScreen
                vc.modalTransitionStyle = .crossDissolve
                self.present(vc, animated: true)
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
        cell.delegate = self
        return cell
    }
}

// MARK: - MessageVideoCellDelegate
extension ChatRoomViewController: MessageCellDelegate {
    func playMedia(url: String, type: Int) {
        if type == 5 {
            let url = URL(string: url)
            let player = AVPlayer(url: url! as URL)
            let playerViewController = AVPlayerViewController()
            playerViewController.player = player
            self.present(playerViewController, animated: true) {
                playerViewController.player!.play()
            }
        } else if type == 4 {
            let photoModalViewController = PhotoModalViewController(imageURLString: url)
            photoModalViewController.modalPresentationStyle = .overFullScreen
            self.present(photoModalViewController, animated: true)
        }
    }
    
    func tapProfile(id: Int) {
        if let roomMember = members.filter({ $0.userId == id }).first,
           let profile = roomMember.profile {
            let profileData = try? JSONDecoder().decode(ModelProfileData.self, from: Data(profile.utf8))
            let profileVC = ProfileModalViewController(profile: ModelProfile(username: roomMember.username,
                                                                             bio: profileData?.message,
                                                                             profileImageURL: profileData?.profile,
                                                                             bgImageURL: profileData?.background))
            profileVC.modalPresentationStyle = .overFullScreen
            self.present(profileVC, animated: true)
        }
    }
}


// MARK: - MessageCollectionViewLayoutDelegate
extension ChatRoomViewController: MessageCollectionViewLayoutDelegate {
    func collectionView(_ collectionView: UICollectionView, heightForCellAtIndexPath indexPath: IndexPath) -> CGFloat {
        let width = collectionView.bounds.width
        let estimatedHeight: CGFloat = 200.0
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
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        return false
    }
}

// MARK: - PHPickerViewControllerDelegate
extension ChatRoomViewController: PHPickerViewControllerDelegate {
    func picker(_ picker: PHPickerViewController, didFinishPicking results: [PHPickerResult]) {
        picker.dismiss(animated: true)
        
        let itemProvider = results.first?.itemProvider
                
        if let itemProvider = itemProvider {
            
            if itemProvider.canLoadObject(ofClass: UIImage.self) {
                itemProvider.loadObject(ofClass: UIImage.self) { [weak self] (image, error) in
                    let imageData = image as? UIImage
                    let resizedImage = imageData?.resized(to: 1024*3)
                    let resizedImageThumbnail = imageData?.resized(to: 512)
                    self?.viewModel.postPhoto(photoFile: resizedImage?.jpegData(compressionQuality: 1.0), photoThumbnail: resizedImageThumbnail?.jpegData(compressionQuality: 1.0))
                }
            } else if itemProvider.hasItemConformingToTypeIdentifier(UTType.movie.identifier) {
                itemProvider.loadFileRepresentation(forTypeIdentifier: UTType.movie.identifier) { [weak self] (videoURL, error) in
                    guard let self = self,
                          let url = videoURL else {
                              return
                          }
                    let asset = AVAsset(url: url)
                    let avAssetImageGenerator = AVAssetImageGenerator(asset: asset)
                    avAssetImageGenerator.appliesPreferredTrackTransform = true
                    let thumnailTime = CMTimeMake(value: 1, timescale: 600)
                    var videoData: Data?
                    var thumbImage: UIImage?
                    do {
                        let cgThumbImage = try avAssetImageGenerator.copyCGImage(at: thumnailTime, actualTime: nil)
                        thumbImage = UIImage(cgImage: cgThumbImage)
                        videoData = try Data(contentsOf: url, options: Data.ReadingOptions.alwaysMapped)
                        self.viewModel.postVideo(videoFile: videoData,
                                                 videoThumbnail: thumbImage?.resized(to: 1024)?.jpegData(compressionQuality: 1.0))
                    } catch {
                        videoData = nil
                        print(error.localizedDescription)
                    }
                }
            }
        }
    }
    
}

