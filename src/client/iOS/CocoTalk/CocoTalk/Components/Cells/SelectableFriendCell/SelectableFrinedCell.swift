//
//  SelectableFrinedCell.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/12.
//

import UIKit
import Then
import SnapKit
import RxSwift

/// 채팅방 친구 초대할 때 보이는 목록에 있는 셀
class SelectableFriendCell: UITableViewCell {
    
    // MARK: - UI Properties
    private let uiView = UIView()
    
    /// 프로필 이미지
    private let ivProfile = ProfileImageView(image: nil)
    
    /// 유저네임
    private let lblUsername = UILabel().then {
        $0.font = .systemFont(ofSize: 16)
        $0.textColor = .label
        $0.textAlignment = .natural
        $0.textColor = .label
    }
    
    /// 체크 박스 이미지 뷰
    private let ivCheckBox = UIImageView().then {
        $0.image = UIImage.init(systemName: "checkmark.circle")!
        $0.tintColor = .systemGreen
    }
    
    // MARK: - Properties
    static let identifier = "SelectableFriendCell"
    
    // MARK: - Init
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        configureView()
        configureSubViews()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    // MARK: - Helper
    private func configureView() {
        uiView.addSubview(ivProfile)
        uiView.addSubview(lblUsername)
        uiView.addSubview(ivCheckBox)
        contentView.addSubview(uiView)
    }
    
    private func configureSubViews() {
        ivProfile.snp.makeConstraints {
            $0.centerY.equalToSuperview()
            $0.leading.equalToSuperview().offset(16)
            $0.width.height.equalTo(44)
        }
        
        lblUsername.snp.makeConstraints {
            $0.centerY.equalToSuperview()
            $0.leading.equalTo(ivProfile.snp.trailing).offset(10)
            $0.trailing.equalToSuperview().inset(30)
        }
        
        ivCheckBox.snp.makeConstraints {
            $0.centerY.equalToSuperview()
            $0.trailing.equalToSuperview().inset(30)
            $0.height.width.equalTo(30)
        }
        
        uiView.snp.makeConstraints {
            $0.edges.equalToSuperview()
            $0.height.equalTo(60)
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
        
        if profile.isSelected  {
            ivCheckBox.image = UIImage(systemName: "checkmark.circle.fill")!
        } else {
            ivCheckBox.image = UIImage(systemName: "checkmark.circle")!
        }
    }
}
