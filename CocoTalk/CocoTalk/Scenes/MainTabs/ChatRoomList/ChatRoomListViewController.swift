//
//  ChatRoomListViewController.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/18.
//

import UIKit
import SnapKit
import Then
import RxSwift

class ChatRoomListViewController: UIViewController {
    
    // MARK: - UI Properties
    /// 네비게이션 바
    private let gnbView = GNBView()
    
    private let tableView = UITableView()
    
    // MARK: - Properties
    var bag = DisposeBag()
    var viewModel: ChatRoomListViewModel
    
    // MARK: - Life cycle
    init() {
        let appDelegate = UIApplication.shared.delegate as! AppDelegate
        let socket = appDelegate.listSocket
        
        viewModel = ChatRoomListViewModel()
        viewModel.dependency.listSocket.accept(socket)
        
        super.init(nibName: nil, bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
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
    
    // MARK: - Helper
    private func fetch() {
        viewModel.fetch()
    }
    
    private func pushChatRoom(_ room: ModelRoom?) {
        let members = room?.members ?? []
        let roomId = room?.id ?? ""
        let vc = ChatRoomViewController(members: members, roomId: roomId)
        vc.title = room?.roomname ?? ""
        vc.hidesBottomBarWhenPushed = true
        guard let nav = self.navigationController else {
            return
        }
        nav.pushViewController(vc, animated: true)
    }
    
    /// 단체 채팅방 초대 메시지 만들기
    private func buildMessage(_ room: ModelRoom) -> ModelPubChatMessage? {
        if let savedData = UserDefaults.standard.object(forKey: UserDefaultsKey.myData.rawValue) as? Data,
           let data = try? JSONDecoder().decode(ModelSignupResponse.self, from: savedData) {
            
            var bundleId: String = room.messageBundleIds ?? ""
            bundleId = String(String(bundleId.dropFirst()).dropLast())
            
            let inviter: RoomMember? = room.members?.filter { $0.userId == data.id }.first
            let invitee: [RoomMember]? = room.members?.filter { $0.userId != data.id }
            
            var inviteText: String = "\(inviter?.username ?? "")님이 "
            invitee?.forEach {
                inviteText = inviteText + "\($0.username ?? "")님, "
            }
            inviteText = String(inviteText.dropLast(2)) + "을 초대했습니다."
            
            let receiverIds: [String] = room.members?.map { "\($0.userId ?? -1)"} ?? []
            
            return ModelPubChatMessage(roomId: room.id,
                                       roomType: room.type ?? 0,
                                       roomname: room.roomname ?? "",
                                       userId: data.id ?? -1,
                                       username: data.username ?? "",
                                       messageBundleId: bundleId,
                                       receiverIds: receiverIds,
                                       type: 1,
                                       content: inviteText)
        }
        return nil
    }
}

// MARK: - BaseViewController
extension ChatRoomListViewController {
    func configureView() {
        tableView.separatorStyle = .none
        tableView.delegate = self
        tableView.dataSource = self
        tableView.register(ChatRoomCell.self, forCellReuseIdentifier: ChatRoomCell.identifier)
        tableView.rowHeight = 76
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
extension ChatRoomListViewController {
    func bindRx() {
        bindViewModel()
        bindSocket()
    }
    
    private func bindViewModel() {
        viewModel.output.rooms
            .subscribe(onNext: { [weak self] rooms in
                guard let self = self else {
                    return
                }
                self.tableView.reloadData()
            }).disposed(by: bag)
    }
    
#warning("방 리스트 바인드")
#warning("다른 뷰에서 새 메시지 바인드")
    private func bindSocket() {
        /// 새 채팅방 생성
        guard let listSocket = viewModel.dependency.listSocket.value else {
            return
        }
        
        listSocket.receviedNewRoom
            .subscribe(onNext: { [weak self] newRoom in
                guard let self = self else {
                    return
                }
                
                guard let socket = self.viewModel.dependency.listSocket.value else {
                    return
                }
                
                guard let newRoom = newRoom,
                      let newMembers = newRoom.members,
                      let requestLog = socket.createChatRequestLog.value,
                      let requestMembers = requestLog.members else {
                          return
                      }
                
                if newRoom.roomname != requestLog.roomname {
                    return
                }
                
                var isSameMembers = true
                let requestMemberIds = requestMembers.map { $0.userId ?? -1 }
                newMembers.forEach {
                    if isSameMembers,
                       !requestMemberIds.contains($0.userId ?? -1) {
                        isSameMembers = false
                    }
                }
                
                if !isSameMembers {
                    return
                }
                
                // 메시지 보내기
                if let roomType = newRoom.type,
                   roomType == 1,
                   let message = self.buildMessage(newRoom) {
                    socket.sendMessage(message)
                }
                self.pushChatRoom(newRoom)
            }).disposed(by: bag)
    }
}


// MARK: - UITableViewDelegate, UITableViewDatasource
extension ChatRoomListViewController: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return viewModel.output.rooms.value.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: ChatRoomCell.identifier, for: indexPath) as! ChatRoomCell
        cell.setData(data: viewModel.output.rooms.value[indexPath.row])
        return cell
    }
    
    // 채팅방 클릭
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let room = viewModel.output.rooms.value[indexPath.row].room
        pushChatRoom(room)
        tableView.deselectRow(at: indexPath, animated: true)
    }
    
}

extension ChatRoomListViewController: GNBDelegate {
    func gnbTabType() -> TabEnum {
        return .chatRoom
    }
    
    func tapAddChat() {
        let createChatVC = CreateChatRoomViewController()
        let nav = UINavigationController(rootViewController: createChatVC)
        nav.modalPresentationStyle = .overFullScreen
        nav.modalTransitionStyle = .coverVertical
        createChatVC.delegate = self
        self.present(nav, animated: true)
    }
    
    func tapSearch() {
        
    }
    
    func tapSetting() {
        
    }
}

extension ChatRoomListViewController: CreateChatRoomDelegate {
    func fetchChatRoom() {
        viewModel.fetch()
    }
}
