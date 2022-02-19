//
//  MessageVideoCellDelegate.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/19.
//

import Foundation

protocol MessageCellDelegate: AnyObject {
    func playMedia(url: String, type: Int)
    func tapProfile(id: Int)
}
