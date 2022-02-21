//
//  ClosableFriendCell.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/12.
//

import Foundation
import UIKit
import Then
import SnapKit

/// 채팅방 초대 대상으로 지정된 친구 셀
class ClosableFriendCell: UICollectionViewCell {
    
    // MARK: - UI Properties
    /// 닫기 버튼
    private let ivClose = UIImageView().then {
        $0.image = UIImage(systemName: "xmark.circle.fill")
        $0.tintColor = .black
    }

    /// 프로필 이미지
    private let ivProfile = ProfileImageView(image: UIImage(named: "profile_noimg_thumbnail_01"))
    
    /// 프로필 유저 네임
    private let lblUsername = UILabel().then {
        $0.font = .systemFont(ofSize: 12)
        $0.textColor = .black
    }
    
    // MARK: - Properties
    static let identifier = "ClosableFriendCell"
    
    // MARK: - Init
    override init(frame: CGRect) {
        super.init(frame: frame)
        configureView()
        configureSubViews()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    // MARK: - Helper
    func configureView() {
        contentView.addSubview(ivProfile)
        contentView.addSubview(ivClose)
        contentView.addSubview(lblUsername)
    }
    
    func configureSubViews() {
        ivProfile.snp.makeConstraints {
            $0.centerX.equalToSuperview()
            $0.top.equalToSuperview().offset(4)
            $0.width.height.equalTo(40)
        }
        
        ivClose.snp.makeConstraints {
            $0.top.equalTo(ivProfile)
            $0.trailing.equalTo(ivProfile).offset(6)
        }
        
        lblUsername.snp.makeConstraints {
            $0.centerX.equalTo(ivProfile)
            $0.top.equalTo(ivProfile.snp.bottom).offset(4)
        }
    }
    
    func setData(data profile: SelectableProfile) {
        if let _img = profile.profileImageUrl,
           !_img.isEmpty {
            let url = URL(string: _img)
            ivProfile.kf.setImage(with: url, placeholder: UIImage(named: "profile_noimg_thumbnail_01"))
        } else {
            ivProfile.image = UIImage(named: "profile_noimg_thumbnail_01")!
        }
        lblUsername.text = profile.username
    }
}

