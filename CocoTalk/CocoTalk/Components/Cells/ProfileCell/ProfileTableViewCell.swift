//
//  ProfileTableViewCell.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/17.
//

import UIKit
import SnapKit
import Kingfisher


/// ProfileTableViewCell
///
/// <기능>
///  - 슬라이드 👉 즐겨찾기, 숨김, 차단
///  - 길게 눌렀을 때 👉 채팅, 프로필 모달뷰
class ProfileTableViewCell: UITableViewCell {
    
    // MARK: - UI properties
    private let uiView = UIView()
    
    /// 프로필 이미지
    private let ivProfile = ProfileImageView(image: nil)
    
    private let stackView = UIStackView().then {
        $0.axis = .vertical
        $0.alignment = .leading
        $0.spacing = 2
    }
    
    /// 이름
    private let lblName = UILabel().then {
        $0.font = .systemFont(ofSize: 16)
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
    static let identifier = "ProfileTableViewCell"
    
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

    override func prepareForReuse() {
        super.prepareForReuse()
        ivProfile.image = UIImage(named: "profile_noimg_thumbnail_01")
    }

    // MARK: - Helper
    private func setUI() {
        stackView.addArrangedSubview(lblName)
        uiView.addSubview(ivProfile)
        uiView.addSubview(stackView)
        contentView.addSubview(uiView)
        
        ivProfile.snp.makeConstraints {
            $0.centerY.equalToSuperview()
            $0.leading.equalToSuperview().offset(16)
            $0.width.height.equalTo(44)
        }
        
        stackView.snp.makeConstraints {
            $0.top.equalToSuperview().offset(10)
            $0.bottom.equalToSuperview().inset(10)
            $0.leading.equalTo(ivProfile.snp.trailing).offset(10)
            $0.trailing.equalToSuperview().inset(30)
        }
        
        uiView.snp.makeConstraints {
            $0.top.bottom.equalToSuperview()
            $0.height.equalTo(60)
        }
    }
    
    func setData(data: ModelProfile) {
        if let _img = data.profileImageUrl,
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
        
        lblName.text = data.name ?? ""
    }
}
