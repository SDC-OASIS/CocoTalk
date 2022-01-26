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
    }
    
    /// 프로필 이미지
    private let ivProfile = ProfileImageView(image: UIImage(named: "profile_noimg_thumnail_01"))
    
    /// 꼬리
    private let ivTail = UIImageView()
    
    /// 텍스트 라벨
    private let lblMessage = PaddingLabel().then {
        $0.font = .systemFont(ofSize: 14.5)
        $0.backgroundColor = .white
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
    
    /// 날짜뷰
    private let lblDate = UILabel().then {
        $0.font = .systemFont(ofSize: 10, weight: .medium)
        $0.textColor = .secondaryLabel
        $0.numberOfLines = 0
        $0.setContentHuggingPriority(.defaultHigh, for: .horizontal)
        $0.setContentCompressionResistancePriority(.defaultHigh, for: .horizontal)
    }
    
    // MARK: - Properties
    static let identifier = "MessageCollectionViewCell"
    let bag = DisposeBag()
    
    var messageId: Int?
    
    var isMe: Bool?
    var userId: Int?
    var hasTail: Bool? // prevUser == currentUser
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
        super.init(frame: .zero)
        setUI()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func prepareForReuse() {
        super.prepareForReuse()
        let width = UIScreen.main.bounds.width
        let height = UIScreen.main.bounds.height
        lblMessage.bounds = CGRect(origin: .zero, size: CGSize(width: width, height: height))
    }
    
    override func preferredLayoutAttributesFitting(_ layoutAttributes: UICollectionViewLayoutAttributes) -> UICollectionViewLayoutAttributes {
        super.preferredLayoutAttributesFitting(layoutAttributes)
        layoutIfNeeded()

        let size = contentView.systemLayoutSizeFitting(layoutAttributes.size)
        var frame = layoutAttributes.frame
        frame.size.height = ceil(size.height)
        layoutAttributes.frame = frame
        return layoutAttributes
    }
    
    // MARK: - Helper
    func setUI() {
        [ivProfile, lblName, ivTail, lblMessage, mediaView, lblDate].forEach {
            contentView.addSubview($0)
        }
        
        ivProfile.snp.makeConstraints {
            $0.top.equalToSuperview().offset(8)
            $0.leading.equalToSuperview().offset(12)
            $0.width.height.equalTo(40)
        }
        
        lblName.snp.makeConstraints {
            $0.top.equalTo(ivProfile).offset(2)
            $0.leading.equalToSuperview().offset(62)
        }
        
        lblMessage.snp.makeConstraints {
            $0.top.equalToSuperview().offset(30)
            $0.leading.equalToSuperview().offset(62)
            $0.trailing.lessThanOrEqualToSuperview()
            $0.bottom.equalToSuperview()
        }
        
        lblDate.snp.makeConstraints {
            $0.leading.equalTo(lblMessage.snp.trailing).offset(4)
            $0.trailing.lessThanOrEqualToSuperview().inset(24)
            $0.bottom.equalTo(lblMessage)
        }
        
        ivTail.image = UIImage(named: "tail_left")
        ivTail.snp.makeConstraints {
            $0.top.equalTo(lblMessage)
            $0.leading.equalTo(lblMessage).offset(-5)
            $0.width.equalTo(12)
            $0.height.equalTo(19)
        }
        
        contentView.snp.makeConstraints {
            $0.edges.equalToSuperview()
        }
    }
    
    func setData(data: ModelMessage) {
        messageId = data.id ?? 0
        lblName.text = "테스트 이름"
        lblMessage.text = data.text ?? ""
        
        if let date = data.date {
            let dateFormatter = DateFormatter()
            dateFormatter.dateFormat = "a h:mm"
            dateFormatter.locale = Locale(identifier: "ko_KR")
            dateFormatter.timeZone = NSTimeZone(name: "ko_KR") as TimeZone?
            lblDate.text = dateFormatter.string(from: date)
        }
    }
}
