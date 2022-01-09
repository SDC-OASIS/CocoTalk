//
//  ViewController.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/06.
//

import UIKit
import SnapKit
import Then

class ViewController: UIViewController {
    
    // MARK: - UI Properties
    private let lblHello = UILabel().then {
        $0.text = "HELLO WORLD!!"
        $0.font = .systemFont(ofSize: 36)
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .white
        
        view.addSubview(lblHello)
        lblHello.snp.makeConstraints {
            $0.center.equalToSuperview()
        }
    }


}

