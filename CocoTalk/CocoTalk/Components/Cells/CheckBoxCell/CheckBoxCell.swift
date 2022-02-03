//
//  CheckBoxCell.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/10.
//

import UIKit
import SnapKit
import Then
import RxSwift
import RxCocoa

class CheckBoxCell: UITableViewCell {
    
    // MARK: - UI Properties
    let ivCheckBox = UIImageView().then {
        $0.image = UIImage(systemName: "checkmark.circle")!
    }
    
    let lblContent = UILabel().then {
        $0.textColor = .label
        $0.font = .systemFont(ofSize: 18)
        $0.numberOfLines = 0
    }
    
    let lblDescription = UILabel().then {
        $0.textColor = .secondaryLabel
        $0.font = .systemFont(ofSize: 14)
        $0.numberOfLines = 0
    }
    
    /// 웹뷰 연결 버튼
    private let btnNext = UIButton().then {
        $0.setImage(UIImage(systemName: "chevron.forward")!, for: .normal)
    }
    
    // MARK: - Properties
    static let identifier = "CheckBoxCellIdentifier"
    var bag = DisposeBag()
    var _bag = DisposeBag()
    var isChecked = false

    // MARK: - Lifecycle
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

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
    override func prepareForReuse() {
        super.prepareForReuse()
        lblContent.text = ""
        lblDescription.text = ""
        bag = DisposeBag()
    }
    
    // MARK: - Helper
    func setUI() {
        [ivCheckBox, lblContent, lblDescription, btnNext].forEach {
            contentView.addSubview($0)
        }
        
        ivCheckBox.snp.makeConstraints {
            $0.leading.equalToSuperview().offset(20)
            $0.top.equalToSuperview().offset(12)
            $0.height.width.equalTo(26)
        }
        
        lblContent.snp.makeConstraints {
            $0.leading.equalTo(ivCheckBox.snp.trailing).offset(10)
            $0.trailing.equalToSuperview().inset(30)
            $0.top.equalTo(ivCheckBox).offset(2)
        }
        
        lblDescription.snp.makeConstraints {
            $0.leading.trailing.equalTo(lblContent)
            $0.top.equalTo(lblContent.snp.bottom).offset(10)
            $0.bottom.equalToSuperview().inset(10)
        }
        
        btnNext.snp.makeConstraints {
            $0.top.equalTo(ivCheckBox)
            $0.trailing.equalToSuperview().inset(20)
        }
    }
    
    func setData(data: TermTableItem) {
        lblContent.text = data.title
        
        if data.isAgreed {
            ivCheckBox.image = UIImage(systemName: "checkmark.circle.fill")!
        } else {
            ivCheckBox.image = UIImage(systemName: "checkmark.circle")!
        }
        
        if let description = data.description {
            lblDescription.text = description
        }

        if let destination = data.destination {
            btnNext.rx.tap.bind {
                print(destination)
                print("destination")
            }
            .disposed(by: _bag)
        }
    }
}
