//
//  TermViewController.swift
//  CocoTalk
//
//  Created by byunghak on 2022/01/10.
//

import UIKit
import SnapKit
import Then
import RxSwift
import RxGesture

class TermViewController: UIViewController {
    
    // MARK: - UI Properties
    private let tableView = UITableView()
    
    private let btnAgree = UIButton().then {
        $0.setTitle("동의하고 계속 진행합니다", for: .normal)
        $0.backgroundColor = .systemBlue
    }
    
    // MARK: - Properties
    let bag = DisposeBag()
    let viewModel = TermViewModel()
    
    // MARK: - Life cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .white
        title = "이용약관에 동의해 주세요"
        
        configureView()
        configureSubviews()
        bindRx()
    }
    
    // MARK: - Helper
}

// MARK: - BaseViewController
extension TermViewController {
    func configureView() {
        tableView.register(CheckBoxCell.self, forCellReuseIdentifier: CheckBoxCell.identifier)
        tableView.delegate = self
        tableView.dataSource = self
        tableView.rowHeight = UITableView.automaticDimension
        tableView.estimatedRowHeight = 50
        
        view.addSubview(tableView)
        view.addSubview(btnAgree)
    }
    
    func configureSubviews() {
        tableView.snp.makeConstraints {
            $0.top.equalTo(view.safeAreaLayoutGuide)
            $0.bottom.equalTo(view.safeAreaLayoutGuide).inset(80)
            $0.leading.trailing.equalToSuperview()
        }
        
        btnAgree.snp.makeConstraints {
            $0.centerX.equalToSuperview()
            $0.top.equalTo(tableView.snp.bottom).offset(10)
            $0.leading.equalToSuperview().offset(20)
            $0.trailing.equalToSuperview().inset(20)
            $0.height.equalTo(60)
        }
    }
}

// MARK: - Bindable
extension TermViewController {
    func bindRx() {
        bindTableView()
        bindButton()
    }
    
    func bindTableView() {
    }
    
    func bindButton() {
        btnAgree.rx.tap
            .subscribe(onNext:{ [weak self] in
                guard let self = self else {
                    return
                }
                let vc = PhoneNumberViewController()
                self.navigationController?.pushViewController(vc, animated: true)
            })
            .disposed(by: bag)
    }
}

// MARK: - UITableViewDelegate
extension TermViewController: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return viewModel.itemList.count
    }
    
    func tableView(_ tableView: UITableView, estimatedHeightForRowAt indexPath: IndexPath) -> CGFloat {
        return UITableView.automaticDimension
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        guard let cell = tableView.dequeueReusableCell(withIdentifier: CheckBoxCell.identifier, for: indexPath) as? CheckBoxCell else { return UITableViewCell() }
        
        let idx = indexPath.row
        cell.setData(data: self.viewModel.itemList[idx])
        cell.selectionStyle = .none
        
        cell.ivCheckBox
            .rx
            .tapGesture()
            .when(.recognized)
            .subscribe(onNext: {[weak self] _ in
                guard let self = self else {
                    return
                }
                self.viewModel.itemList[idx].isAgreed.toggle()
                self.tableView.reloadData()
            })
            .disposed(by: cell.bag)
        return cell
        
    }
}


// MARK: - TermTableItem
struct TermTableItem {
    var title: String
    var description: String?
    var destination: String?
    var isAgreed: Bool
}
