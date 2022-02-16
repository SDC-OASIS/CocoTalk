//
//  MessageCollectionViewCell.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/21.
//

import UIKit
import SnapKit
import Then
import RxSwift

class MessageCollectionViewCell: UICollectionViewCell {
    
    // MARK: - UI Properties
    /// 이름
    private let lblName = UILabel().then {
        $0.font = .systemFont(ofSize: 13)
        $0.textColor = .secondaryLabel
        $0.isHidden = true
    }
    
    /// 프로필 이미지
    private let ivProfile = ProfileImageView(image: UIImage(named: "profile_noimg_thumbnail_01")).then {
        $0.isHidden = true
    }
    
    /// 꼬리
    private let ivTail = UIImageView().then {
        $0.isHidden = true
    }
    
    /// 텍스트 라벨
    private let lblMessage = PaddingLabel().then {
        $0.font = .systemFont(ofSize: 14.5)
        $0.numberOfLines = 0
        $0.lineBreakStrategy = .pushOut
        $0.layer.cornerRadius = 14
        $0.clipsToBounds = true
        $0.setContentHuggingPriority(.defaultLow, for: .horizontal)
        $0.setContentCompressionResistancePriority(.defaultLow, for: .horizontal)
    }
    
    /// 미디어 뷰
    /// - URL, 사진, 동영상
    private let mediaView = UIView().then {
        $0.backgroundColor = .gray
        $0.layer.cornerRadius = 14
        $0.isHidden = true
    }
    
    /// 메시지 메타 데이터 스택
    private let stackView = UIStackView().then {
        $0.axis = .vertical
        $0.setContentHuggingPriority(.defaultHigh, for: .horizontal)
        $0.setContentCompressionResistancePriority(.defaultHigh, for: .horizontal)
    }
    
    /// 안 읽은 사람 수
    private let lblUnreadMemberCount = UILabel().then {
        $0.font = .systemFont(ofSize: 10, weight: .bold)
        $0.textColor = UIColor(red: 252/255, green: 235/255, blue: 88/255, alpha: 1)
        $0.numberOfLines = 0
    }
    
    /// 날짜뷰
    private let lblDate = UILabel().then {
        $0.font = .systemFont(ofSize: 10, weight: .regular)
        $0.textColor = .secondaryLabel
        $0.numberOfLines = 0
    }
    
    // MARK: - Properties
    static let identifier = "MessageCollectionViewCell"
    let bag = DisposeBag()
    
    var messageId: String?
    
    var isMe: Bool?
    var userId: Int?
    var hasTail: Bool? // prevUser != currentUser
    var withDate: Bool?
    var hasUrl: Bool?
    var isReply: Bool?
    var repliedAt: Int? // messageId
    var userTags: Int? // userId
    
#warning("enum으로?")
    var isEmoji: Bool?
    var isVideo: Bool?
    var isPhoto: Bool?
    
    // MARK: - Lifecycle
    override init(frame: CGRect) {
        super.init(frame: frame)
        configureView()
        configureSubViews()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func layoutSubviews() {
        configureSubViews()
        super.layoutSubviews()
    }
    
    override func prepareForReuse() {
        ivProfile.image = nil
        lblMessage.text = ""
        lblName.text = ""
        lblDate.text = ""
        hasTail = false
        withDate = false
        lblUnreadMemberCount.text = ""
        
        super.prepareForReuse()
    }
    
    // MARK: - Helper
    func configureView() {
        stackView.addArrangedSubview(lblUnreadMemberCount)
        stackView.addArrangedSubview(lblDate)
        [ivProfile, ivTail, lblName, lblMessage, mediaView, stackView].forEach {
            contentView.addSubview($0)
        }
    }
    
    func configureSubViews() {
        let width = UIScreen.main.bounds.width
        let height = UIScreen.main.bounds.height
        lblMessage.bounds = CGRect(origin: .zero, size: CGSize(width: width, height: height))
        lblDate.bounds = CGRect(origin: .zero, size: CGSize(width: width, height: height))
        [ivProfile, ivTail, lblName, lblMessage, mediaView, stackView].forEach {
            $0.snp.removeConstraints()
        }
        
        ivProfile.isHidden = true
        ivTail.isHidden = true
        lblName.isHidden = true
        
        let hasTail = self.hasTail ?? false
        let isMe = self.isMe ?? false
        
        if hasTail {
            ivTail.isHidden = false
            
            if isMe {
                ivTail.snp.makeConstraints {
                    $0.top.equalTo(lblMessage)
                    $0.leading.equalTo(lblMessage.snp.trailing).offset(-7)
                    $0.width.equalTo(12)
                    $0.height.equalTo(19)
                }
            } else {
                ivProfile.isHidden = false
                lblName.isHidden = false
                
                ivProfile.snp.makeConstraints {
                    $0.top.equalToSuperview().offset(8)
                    $0.leading.equalToSuperview().offset(12)
                    $0.width.height.equalTo(36)
                }
                
                lblName.snp.makeConstraints {
                    $0.top.equalTo(ivProfile).offset(2)
                    $0.leading.equalToSuperview().offset(62)
                }
                
                ivTail.snp.makeConstraints {
                    $0.top.equalTo(lblMessage)
                    $0.leading.equalTo(lblMessage).offset(-4.5)
                    $0.width.equalTo(12)
                    $0.height.equalTo(19)
                }
            }
            
        }
        
        
        if isMe {
            lblMessage.backgroundColor = UIColor(red: 250/255, green: 230/255, blue: 76/255, alpha: 1)
            lblMessage.snp.makeConstraints {
                $0.top.equalToSuperview()
                $0.trailing.equalToSuperview().offset(-20)
                $0.leading.greaterThanOrEqualToSuperview()
                $0.bottom.equalToSuperview()
            }
            
            stackView.alignment = .trailing
            stackView.snp.makeConstraints {
                $0.trailing.equalTo(lblMessage.snp.leading).offset(-4)
                $0.leading.greaterThanOrEqualToSuperview().offset(56)
                $0.bottom.equalTo(lblMessage)
            }
        } else {
            lblMessage.backgroundColor = .white
            lblMessage.snp.makeConstraints {
                $0.top.equalToSuperview().offset(hasTail ? 30 : 0)
                $0.leading.equalToSuperview().offset(56)
                $0.trailing.lessThanOrEqualToSuperview()
                $0.bottom.equalToSuperview()
            }
            
            stackView.alignment = .leading
            stackView.snp.makeConstraints {
                $0.leading.equalTo(lblMessage.snp.trailing).offset(4)
                $0.trailing.lessThanOrEqualToSuperview().inset(24)
                $0.bottom.equalTo(lblMessage)
            }
        }
    }
    
    func setData(data: ModelMessage) {
        if let profileImgUrl = data.profileImageURL,
           !profileImgUrl.isEmpty {
            let url = URL(string: profileImgUrl)
            ivProfile.kf.setImage(with: url, placeholder: UIImage(named: "profile_noimg_thumbnail_01"))
        } else {
            ivProfile.image = UIImage(named: "profile_noimg_thumbnail_01")
        }
        
        messageId = data.id ?? ""
        lblName.text = data.username ?? ""
        lblMessage.text = data.content ?? ""
        isMe = data.isMe ?? false
        hasTail = data.hasTail ?? false
        ivTail.image = (isMe ?? false) ? UIImage(named: "tail_right") : UIImage(named: "tail_left")
        
        if let count = data.unreadMemberCount {
            if count > 0 {
                lblUnreadMemberCount.text = "\(count)"
            } else {
                lblUnreadMemberCount.text = ""
            }
        }
        
        if let hasData = data.hasDate,
           hasData,
           let sentAt = data.sentAt,
           let dateLabel = sentAt.parseDateStringForMessage() {
            lblDate.text = dateLabel
        }
        
        setNeedsLayout()
    }
}
