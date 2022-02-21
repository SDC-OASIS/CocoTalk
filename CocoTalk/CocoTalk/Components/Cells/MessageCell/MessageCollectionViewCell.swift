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
import RxCocoa
import AVKit

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
    
    /// 사진, 영상 뷰
    private let ivMedia = UIImageView().then {
        $0.contentMode = .scaleAspectFit
        $0.layer.cornerRadius = 14
        $0.clipsToBounds = true
        $0.setContentHuggingPriority(.defaultLow, for: .horizontal)
        $0.setContentCompressionResistancePriority(.defaultLow, for: .horizontal)
        $0.isHidden = true
    }
    
    private let ivVideoIndicator = UIImageView().then {
        $0.image = UIImage(systemName: "play.circle")
        $0.isHidden = true
        $0.tintColor = .white
        $0.layer.zPosition = 999
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
        $0.textColor = UIColor(red: 116/255, green: 159/255, blue: 88/255, alpha: 1)
//        $0.textColor = UIColor(red: 252/255, green: 235/255, blue: 88/255, alpha: 1)
        $0.numberOfLines = 0
    }
    
    /// 날짜뷰
    private let lblDate = UILabel().then {
        $0.font = .systemFont(ofSize: 10, weight: .regular)
        $0.textColor = .secondaryLabel
        $0.numberOfLines = 0
    }
    
    /// 파일 다운로드 뷰
    private let uiviewFile = UIView().then {
        $0.layer.cornerRadius = 14
        $0.clipsToBounds = true
        $0.setContentHuggingPriority(.defaultLow, for: .horizontal)
        $0.setContentCompressionResistancePriority(.defaultLow, for: .horizontal)
        $0.isHidden = true
    }
    
    /// 파일 다운로드 버튼 뷰
    private let btnViewFile = UIView().then {
        $0.layer.cornerRadius = 20
        $0.clipsToBounds = true
        $0.backgroundColor = UIColor(red: 250/255, green: 250/255, blue: 250/255, alpha: 1)
    }
    
    /// 파일 다운로드 이미지
    private let ivArrowDown = UIImageView().then {
        $0.image = UIImage(systemName: "arrow.down.to.line")
        $0.tintColor = .black
    }
    
    /// 파일 URL
    private let lblFileURL = UILabel().then {
        $0.font = .systemFont(ofSize: 14.5)
        $0.textColor = .black
        $0.lineBreakMode = .byTruncatingMiddle
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
    
    var isEmoji: Bool?
    var isVideo: Bool?
    var isPhoto: Bool?
    var isSystemMessage: Bool?
    var isFile: Bool?
    
    var delegate: MessageCellDelegate?
    
    // MARK: - Lifecycle
    override init(frame: CGRect) {
        super.init(frame: frame)
        configureView()
        configureSubViews()
        bindProfile()
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
        ivMedia.image = nil
        lblMessage.text = ""
        lblName.text = ""
        lblDate.text = ""
        hasTail = false
        withDate = false
        isVideo = false
        isPhoto = false
        isSystemMessage = false
        isFile = false
        lblUnreadMemberCount.text = ""
        
        super.prepareForReuse()
    }
    
    // MARK: - Helper
    func configureView() {
        stackView.addArrangedSubview(lblUnreadMemberCount)
        stackView.addArrangedSubview(lblDate)
        btnViewFile.addSubview(ivArrowDown)
        
        [btnViewFile, lblFileURL].forEach {
            uiviewFile.addSubview($0)
        }
        [ivProfile, ivTail, lblName, lblMessage, ivVideoIndicator, ivMedia, uiviewFile, stackView].forEach {
            contentView.addSubview($0)
        }
    }
    
    func configureSubViews() {
        let width = UIScreen.main.bounds.width
        let height = UIScreen.main.bounds.height
        lblMessage.bounds = CGRect(origin: .zero, size: CGSize(width: width, height: height))
        lblDate.bounds = CGRect(origin: .zero, size: CGSize(width: width, height: height))
        [ivProfile, ivTail, lblName, lblMessage, ivVideoIndicator, ivMedia, uiviewFile, btnViewFile, lblFileURL, ivArrowDown, stackView].forEach {
            $0.snp.removeConstraints()
        }
        
        // 메타 데이터 초기화
        ivProfile.isHidden = true
        ivTail.isHidden = true
        lblName.isHidden = true
        ivVideoIndicator.isHidden = true
        lblMessage.isHidden = true
        ivMedia.isHidden = true
        uiviewFile.isHidden = true
        
        if isSystemMessage ?? false {
            lblMessage.snp.makeConstraints {
                $0.centerX.equalToSuperview()
                $0.top.bottom.equalToSuperview()
            }
            lblMessage.textColor = .white
            lblMessage.font = .systemFont(ofSize: 11)
            lblMessage.backgroundColor = UIColor(red: 0, green: 0, blue: 0, alpha: 0.15)
            lblMessage.isHidden = false
            lblUnreadMemberCount.isHidden = true
            lblDate.isHidden = true
            return
        } else {
            lblMessage.font = .systemFont(ofSize: 14.5)
            lblMessage.textColor = .black
        }
        
        let mainContent: UIView
        if isPhoto ?? false || isVideo ?? false { // 사진, 영상 메시지의 경우
            mainContent = ivMedia
            ivMedia.isHidden = false
            if isVideo ?? false {
                ivVideoIndicator.isHidden = false
                ivVideoIndicator.snp.makeConstraints { // 영상 메시지일 경우
                    $0.center.equalTo(ivMedia)
                    $0.width.height.equalTo(50)
                }
            }
        }  else if isFile ?? false { // 파일 메시지일 경우
            mainContent = uiviewFile
            btnViewFile.snp.makeConstraints {
                $0.centerY.equalToSuperview()
                $0.leading.equalToSuperview().offset(7.5)
                $0.height.width.equalTo(40)
            }
            
            ivArrowDown.snp.makeConstraints {
                $0.center.equalTo(btnViewFile)
                $0.width.height.equalTo(20)
            }
            
            lblFileURL.snp.makeConstraints {
                $0.centerY.equalTo(btnViewFile)
                $0.leading.equalTo(btnViewFile.snp.trailing).offset(10)
                $0.trailing.equalToSuperview().inset(20)
            }
            uiviewFile.isHidden = false
        } else { // 일반 텍스트 메시지일 경우
            mainContent = lblMessage
            lblMessage.isHidden = false
        }
        
        let hasTail = self.hasTail ?? false
        let isMe = self.isMe ?? false
        
        if hasTail {
            ivTail.isHidden = false
            if isMe {
                ivTail.snp.makeConstraints {
                    $0.top.equalTo(lblMessage)
                    $0.leading.equalTo(mainContent.snp.trailing).offset(-7)
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
                    $0.top.equalTo(mainContent)
                    $0.leading.equalTo(mainContent).offset(-4.5)
                    $0.width.equalTo(12)
                    $0.height.equalTo(19)
                }
            }
            
        }
        
        let aspectRatio: CGFloat?
        if let imageWidth = ivMedia.image?.size.width,
           let imageHeight = ivMedia.image?.size.height {
            aspectRatio = imageWidth/imageHeight
        } else {
            aspectRatio = nil
        }
        
        if isMe {
            mainContent.backgroundColor = UIColor(red: 250/255, green: 230/255, blue: 76/255, alpha: 1)
            mainContent.snp.makeConstraints {
                $0.top.equalToSuperview()
                $0.trailing.equalToSuperview().offset(-20)
                $0.leading.greaterThanOrEqualToSuperview()
                if isPhoto ?? false || isVideo ?? false {
                    $0.width.equalTo(200)
                    $0.height.equalTo(200).multipliedBy(1/(aspectRatio ?? 1))
                } else if isFile ?? false {
                    $0.height.equalTo(60)
                    $0.width.equalTo(260)
                } else {
                    $0.bottom.equalToSuperview()
                }
            }
            
            stackView.alignment = .trailing
            stackView.snp.makeConstraints {
                $0.trailing.equalTo(mainContent.snp.leading).offset(-4)
                $0.leading.greaterThanOrEqualToSuperview().offset(56)
                $0.bottom.equalTo(mainContent)
            }
        } else {
            mainContent.backgroundColor = .white
            mainContent.snp.makeConstraints {
                $0.top.equalToSuperview().offset(hasTail ? 30 : 0)
                $0.leading.equalToSuperview().offset(56)
                $0.trailing.lessThanOrEqualToSuperview()
                $0.bottom.equalToSuperview()
                if isPhoto ?? false || isVideo ?? false {
                    $0.height.equalTo(200).multipliedBy(1/(aspectRatio ?? 1))
                    $0.width.equalTo(200)
                } else if isFile ?? false {
                    $0.height.equalTo(60)
                    $0.width.equalTo(260)
                }
            }
            
            stackView.alignment = .leading
            stackView.snp.makeConstraints {
                $0.leading.equalTo(mainContent.snp.trailing).offset(4)
                $0.trailing.lessThanOrEqualToSuperview().inset(24)
                $0.bottom.equalTo(mainContent)
            }
        }
    }
    
    func setData(data: ModelMessage) {
        // 메타 데이터 초기화
        hasTail = false
        withDate = false
        isVideo = false
        isPhoto = false
        isSystemMessage = false
        isFile = false
        
        let messageType = data.type ?? 0
        if (1...3).contains(messageType) {
            lblMessage.text = data.content ?? ""
            lblMessage.textAlignment = .center
            isSystemMessage = true
            setNeedsLayout()
            return
        } else {
            lblMessage.textAlignment = .natural
        }
        
        // 프로필 이미지 수정
        self.userId = data.userId
        if let profileImgUrl = data.profileImageURL,
           !profileImgUrl.isEmpty {
            let url = URL(string: profileImgUrl)
            ivProfile.kf.setImage(with: url, placeholder: UIImage(named: "profile_noimg_thumbnail_01"))
        } else {
            ivProfile.image = UIImage(named: "profile_noimg_thumbnail_01")
        }
        
        messageId = data.id ?? ""
        lblName.text = data.username ?? "(알수 없음)"
        
        if messageType == 4 { // 이미지 셀 설정
            isPhoto = true
            if let mediaUrl = data.content,
               !mediaUrl.isEmpty {
                let url = URL(string: mediaUrl)
                lblMessage.text = mediaUrl
                ivMedia.kf.setImage(with: url, placeholder: UIImage(systemName: "icloud.slash"))
                bindMediaButton()
            } else {
                ivMedia.image = UIImage(systemName: "icloud.slash")
            }
        } else if messageType == 5 { // 영상 셀 설정
            isVideo = true
            if let mediaUrl = data.content,
               !mediaUrl.isEmpty {
                lblMessage.text = mediaUrl
                let filename: NSString = mediaUrl as NSString
                let pathPrefix = filename.deletingPathExtension
                let url = URL(string: "\(pathPrefix)_th.jpeg")
                ivMedia.kf.setImage(with: url, placeholder: UIImage(systemName: "icloud.slash"))
                bindMediaButton()
            } else {
                ivMedia.image = UIImage(systemName: "icloud.slash")
            }
        } else if messageType == 6 { // 그 외 파일 셀 설정
            isFile = true
            lblFileURL.text = data.content ?? ""
        }
        lblMessage.text = data.content ?? ""
        
        isMe = data.isMe ?? false
        hasTail = data.hasTail ?? false
        ivTail.image = (isMe ?? false) ? UIImage(named: "tail_right") : UIImage(named: "tail_left")
        
        // 안 읽은 사람 수 보여주기
        if let count = data.unreadMemberCount {
            if count > 0 {
                lblUnreadMemberCount.isHidden = false
                lblUnreadMemberCount.text = "\(count)"
            } else {
                lblUnreadMemberCount.text = ""
            }
        }
        
        // 날짜 보여주기 설정
        if let hasData = data.hasDate,
           hasData,
           let sentAt = data.sentAt,
           let dateLabel = sentAt.parseDateStringForMessage() {
            lblDate.isHidden = false
            lblDate.text = dateLabel
        }
        
        setNeedsLayout()
    }
    
    func bindMediaButton() {
        // 앨범 첨부 파일 전송
        ivMedia.rx.tapGesture()
            .when(.ended)
            .subscribe(onNext: { [weak self] _ in
                guard let self = self,
                      let urlString = self.lblMessage.text else {
                    return
                }
                if self.isVideo ?? false {
                    self.delegate?.playMedia(url: urlString, type: 5)
                } else if self.isPhoto ?? false {
                    self.delegate?.playMedia(url: urlString, type: 4)
                }
            }).disposed(by: bag)
    }
    
    func bindProfile() {
        // 프로필 탭 했을 때 동작
        ivProfile.rx.tapGesture()
            .when(.ended)
            .subscribe(onNext: { [weak self] _ in
                guard let self = self else {
                    return
                }
                self.delegate?.tapProfile(id: self.userId ?? -1)
            }).disposed(by: bag)
    }
}
