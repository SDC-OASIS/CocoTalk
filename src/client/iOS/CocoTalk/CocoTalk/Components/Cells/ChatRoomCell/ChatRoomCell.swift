//
//  ChatRoomCell.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/18.
//

import UIKit
import SnapKit
import Kingfisher

/// 채팅방 목록 셀
class ChatRoomCell: UITableViewCell {
    
    // MARK: - UI properties
    private let uiView = UIView()
    
    #warning("단체톡 이미지 처리")
    // 갠톡, 3인, 4인 이상
    /// 프로필 이미지
    private let ivRoomImage = ProfileImageView(image: nil)
    
    private let centerStackView = UIStackView().then {
        $0.axis = .vertical
        $0.alignment = .leading
        $0.spacing = 2
    }
    
    private let titleStackView = UIStackView().then {
        $0.axis = .horizontal
        $0.alignment = .center
        $0.spacing = 4
    }
    
    /// "나" 아이콘
    private let ivMe = UIImageView(image: UIImage(named: "my_chatroom_indicator")!).then {
        $0.isHidden = true
    }
    
    /// 채팅방 이름
    private let lblTitle = UILabel().then {
        $0.font = .systemFont(ofSize: 16)
        $0.textColor = .label
        $0.textAlignment = .natural
        $0.textColor = .label
    }
    
    /// 톡방 멤버 수
    private let lblMemeberNumber = UILabel().then {
        $0.font = .systemFont(ofSize: 16)
        $0.textAlignment = .natural
        $0.textColor = .tertiaryLabel
    }
    
    /// 톡방 상단 고정 아이콘
    private let ivPin = UIImageView(image: UIImage(systemName: "pin.fill")!).then {
        $0.isHidden = true
        $0.tintColor = .lightGray
    }
    
    /// 톡방 알림 해제 아이콘
    private let ivSilent = UIImageView(image: UIImage(systemName: "bell.slash.fill")!).then {
        $0.isHidden = true
        $0.tintColor = .lightGray
    }
    
    /// 가장 최근 메시지
    private let lblLastMessage = UILabel().then {
        $0.font = .systemFont(ofSize: 13)
        $0.textAlignment = .natural
        $0.textColor = .secondaryLabel
        $0.numberOfLines = 2
        $0.lineBreakMode = .byTruncatingTail
    }
    
    /// 가장 최근 메시지 시간
    private let lblLastMsgTime = UILabel().then {
        $0.font = .systemFont(ofSize: 12)
        $0.textAlignment = .right
        $0.textColor = .tertiaryLabel
    }
    
    /// 안읽은 메시지 수
    private let viewUnreadNumberContainer = UIView().then {
        $0.backgroundColor = .red
        $0.layer.cornerRadius = 12
    }

    private let lblUnreadMessageNumber = UILabel().then {
        $0.font = .systemFont(ofSize: 12, weight: .bold)
        $0.textColor = .white
        $0.textAlignment = .center
    }
    
    // MARK: - Properties
    static let identifier = "ChatRoomTableViewCell"
    
    // MARK: - Init
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        setUI()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()

    }
    
    #warning("이미지 재사용을 위한 처리 필요")
    override func prepareForReuse() {
        super.prepareForReuse()
    }

    // MARK: - Helper
    private func setUI() {
        
        titleStackView.addArrangedSubview(ivMe)
        titleStackView.addArrangedSubview(lblTitle)
        titleStackView.addArrangedSubview(lblMemeberNumber)
        titleStackView.addArrangedSubview(ivPin)
        titleStackView.addArrangedSubview(ivSilent)
        centerStackView.addArrangedSubview(titleStackView)
        centerStackView.addArrangedSubview(lblLastMessage)
        uiView.addSubview(lblLastMsgTime)
        viewUnreadNumberContainer.addSubview(lblUnreadMessageNumber)
        uiView.addSubview(viewUnreadNumberContainer)
        uiView.addSubview(ivRoomImage)
        uiView.addSubview(centerStackView)
        contentView.addSubview(uiView)
        
        ivRoomImage.snp.makeConstraints {
            $0.centerY.equalToSuperview()
            $0.leading.equalToSuperview().offset(16)
            $0.width.height.equalTo(50)
        }
        
        ivMe.snp.makeConstraints {
            $0.width.height.equalTo(14)
        }
        
        ivPin.snp.makeConstraints {
            $0.width.height.equalTo(14)
        }
        
        ivSilent.snp.makeConstraints {
            $0.width.height.equalTo(14)
        }
        
        lblTitle.snp.makeConstraints {
            $0.width.lessThanOrEqualTo(150)
        }
        
        titleStackView.snp.makeConstraints {
            $0.width.lessThanOrEqualToSuperview()
            $0.height.equalTo(22)
        }
        
        lblLastMsgTime.snp.makeConstraints {
            $0.centerY.equalTo(titleStackView)
            $0.trailing.equalToSuperview().inset(16)
        }
        
        lblUnreadMessageNumber.snp.makeConstraints {
            $0.center.equalToSuperview()
        }
        
        viewUnreadNumberContainer.snp.makeConstraints {
            $0.width.height.equalTo(24)
            $0.trailing.equalTo(lblLastMsgTime)
            $0.centerY.equalTo(lblLastMessage)
        }
        
        centerStackView.snp.makeConstraints {
            $0.top.equalToSuperview().offset(10)
            $0.bottom.equalToSuperview().inset(10)
            $0.leading.equalTo(ivRoomImage.snp.trailing).offset(16)
            $0.trailing.equalTo(lblLastMsgTime.snp.leading).offset(-10)
        }
        
        uiView.snp.makeConstraints {
            $0.top.bottom.width.equalToSuperview()
            $0.height.equalTo(76)
        }
    }
    
    func setData(data: ModelRoomList) {
        
        if (data.room?.type ?? 0) == 0 { // 개인톡
            var member = data.room?.members?.filter { $0.userId != UserRepository.myProfile?.id }.first
            member = ProfileHelper().decodeProfile(profile: member!)
            lblTitle.text = member?.username ?? "(알수 없음)"
            let imgURLString = member?.profileImageURL ?? ""
            if let url = URL(string: imgURLString) {
                ivRoomImage.kf.setImage(with: url, placeholder: UIImage(named: "profile_noimg_thumbnail_01")!)
            } else {
                ivRoomImage.image = UIImage(named: "profile_noimg_thumbnail_01")!
            }
            
        } else {
            lblTitle.text = data.room?.roomname ?? ""
            let imgURLString = data.room?.img ?? ""
            if let url = URL(string: imgURLString) { // 단체톡
                ivRoomImage.kf.setImage(with: url, placeholder: UIImage(named: "profile_noimg_thumbnail_01")!)
            } else {
                ivRoomImage.image = UIImage(named: "profile_noimg_thumbnail_01")!
            }
        }

        lblMemeberNumber.text = "\(data.room?.members?.count ?? -1)"
        ivPin.isHidden = !(data.room?.isPinned ?? false)
        ivSilent.isHidden = !(data.room?.isSilent ?? false)
        
        switch data.recentChatMessage?.type ?? 0  {
        case 4:
            lblLastMessage.text = "사진을 보냈습니다."
            break
        case 5:
            lblLastMessage.text = "동영상을 보냈습니다."
            break
        case 6:
            lblLastMessage.text = "파일을 보냈습니다."
            break
        default:
            lblLastMessage.text = data.recentChatMessage?.content ?? ""
        }
        
        
        if let sentAt = data.recentChatMessage?.sentAt {
            let dateFormatter = DateFormatter()
            dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS"
            
            if var sentAtDate = dateFormatter.date(from: sentAt) {
                let myDateFormatter = DateFormatter()
                myDateFormatter.dateFormat = "yyyy.MM.dd a hh:mm"
                myDateFormatter.locale = Locale(identifier:"ko_KR")
                sentAtDate.convertToKorTime()
                
                lblLastMsgTime.text = myDateFormatter.string(from: sentAtDate)
            }
        }

        if data.unreadNumber ?? 0 > 0 {
            viewUnreadNumberContainer.backgroundColor = .red
            lblUnreadMessageNumber.isHidden = false
            lblUnreadMessageNumber.text = (data.unreadNumber ?? 0).description
        } else {
            viewUnreadNumberContainer.backgroundColor = .clear
            lblUnreadMessageNumber.isHidden = true
        }
    }
}
