//
//  MyProfileCell.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/17.
//

import UIKit
import SnapKit

/// MyProfileCell
///
/// <기능>
///  - 꾹 눌렀을 때 나와의 채팅
class MyProfileCell: UITableViewCell {
    
    // MARK: - UI properties
    /// 프로필 이미지
    private let ivProfile = ProfileImageView(image: nil)
    
    private let stackView = UIStackView().then {
        $0.axis = .vertical
        $0.alignment = .leading
        $0.spacing = 2
    }
    
    /// 이름
    private let lblName = UILabel().then {
        $0.font = .systemFont(ofSize: 18)
        $0.textColor = .label
        $0.textAlignment = .natural
        $0.textColor = .label
    }
    
    /// 상태 메시지
    private let lblBio = UILabel().then {
        $0.font = .systemFont(ofSize: 14)
        $0.textAlignment = .natural
        $0.textColor = .secondaryLabel
        $0.numberOfLines = 1
        $0.lineBreakMode = .byTruncatingTail
    }
    
    // MARK: - Properties
    static let identifier = "MyProfileCell"
    
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

    // MARK: - Helper
    #warning("이미지 재사용을 위한 처리 필요")
    override func prepareForReuse() {
        super.prepareForReuse()
    }
    
    private func setUI() {
        stackView.addArrangedSubview(lblName)
        contentView.addSubview(ivProfile)
        contentView.addSubview(stackView)
        
        ivProfile.snp.makeConstraints {
            $0.centerY.equalToSuperview()
            $0.top.equalToSuperview().offset(4)
            $0.bottom.equalToSuperview().inset(4)
            $0.leading.equalToSuperview().offset(16)
            $0.width.height.equalTo(60)
        }
        
        stackView.snp.makeConstraints {
            $0.top.equalToSuperview().offset(10)
            $0.bottom.equalToSuperview().inset(10)
            $0.leading.equalTo(ivProfile.snp.trailing).offset(10)
            $0.trailing.equalToSuperview().inset(30)
        }
    }
    
    func setData(data: ModelProfile) {
        if let _img = data.profileImageURL,
           !_img.isEmpty {
            let url = URL(string: _img)
            ivProfile.kf.setImage(with: url, placeholder: UIImage(named: "profile_noimg_thumbnail_01"))
        } else {
            ivProfile.image = UIImage(named: "profile_noimg_thumbnail_01")!
        }
        
        if let _bio = data.bio {
            lblBio.text = _bio
            stackView.addArrangedSubview(lblBio)
        } else {
            lblBio.isHidden = true
        }
        
        lblName.text = data.username ?? ""
    }
}
