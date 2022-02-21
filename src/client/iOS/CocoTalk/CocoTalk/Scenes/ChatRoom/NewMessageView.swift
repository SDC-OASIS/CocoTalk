//
//  NewMessageView.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/20.
//

import UIKit
import SnapKit
import Then

/// 채팅방 내에 새 메시지 뷰
///
/// 탭 하면 최하단으로 이동.
/// 
/// 탭 하지 않고 최하단으로 가면 사라짐
class NewMessageView: UIView {

    // MARK: - UI properties
    private let uiview = UIView().then {
        $0.backgroundColor = .white
        $0.layer.cornerRadius = 10
        $0.clipsToBounds = true
    }
    
    /// 프로필 이미지 뷰
    private let ivProfile = ProfileImageView(image: UIImage(named: "profile_noimg_thumbnail_01"))
    
    /// 유저네임
    private let lblUsername = UILabel().then {
        $0.font = .systemFont(ofSize: 13)
        $0.textColor = .secondaryLabel
        $0.setContentHuggingPriority(.defaultHigh, for: .horizontal)
        $0.setContentCompressionResistancePriority(.defaultHigh, for: .horizontal)
    }
    
    /// 컨텐츠
    private let lblContent = UILabel().then {
        $0.font = .systemFont(ofSize: 13)
        $0.textColor = .black
        $0.lineBreakMode = .byTruncatingMiddle
        $0.textAlignment = .natural
        $0.setContentHuggingPriority(.defaultLow, for: .horizontal)
        $0.setContentCompressionResistancePriority(.defaultLow, for: .horizontal)
    }
    
    /// 아래 화살표 이미지
    private let ivDownArrow = UIImageView().then {
        $0.image = UIImage(systemName: "chevron.down")
        $0.tintColor = .lightGray
    }
    
    // MARK: - Properties
    
    // MARK: - Init
    init() {
        super.init(frame: .zero)
        configureView()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    // MARK: - Helpers
    
    func setData(profileImageURL: String? = nil, username: String? = nil, content: String? = nil) {
        let profileURL = profileImageURL ?? ""
        
        if !profileURL.isEmpty {
            let url = URL(string: profileURL)
            ivProfile.kf.setImage(with: url, placeholder: UIImage(named: "profile_noimg_thumbnail_01"))
        } else {
            ivProfile.image = UIImage(named: "profile_noimg_thumbnail_01")!
        }
        
        lblUsername.text = username ?? ""
        lblContent.text = content ?? ""
    }
 
    private func configureView() {
        [uiview, ivProfile, lblUsername, lblContent, ivDownArrow].forEach {
            addSubview($0)
        }
        
        uiview.snp.makeConstraints {
            $0.leading.equalToSuperview().offset(10)
            $0.trailing.equalToSuperview().inset(10)
            $0.height.bottom.equalToSuperview()
        }
        
        ivProfile.snp.makeConstraints {
            $0.leading.equalToSuperview().offset(15)
            $0.height.width.equalTo(26)
            $0.centerY.equalToSuperview()
        }
        
        lblUsername.snp.makeConstraints {
            $0.leading.equalTo(ivProfile.snp.trailing).offset(4)
            $0.centerY.equalToSuperview()
        }
        
        lblContent.snp.makeConstraints {
            $0.leading.equalTo(lblUsername.snp.trailing).offset(4)
            $0.trailing.equalTo(ivDownArrow.snp.leading).offset(-10)
            $0.centerY.equalToSuperview()
        }
        
        ivDownArrow.snp.makeConstraints {
            $0.centerY.equalToSuperview()
            $0.trailing.equalToSuperview().offset(-20)
            $0.height.width.equalTo(20)
        }
    }
}
