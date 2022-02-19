//
//  Date+Ext.swift
//  CocoTalk
//
//  Created by byunghak on 2022/02/15.
//

import Foundation

extension Date {
    
    /// GMT+00 시각을 GMT+09 시각으로 변환
    mutating func convertToKorTime() {
        self.addTimeInterval(3600*9)
    }
    
    func getMinute() -> Int? {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "mm"
        return Int(dateFormatter.string(from: self))
    }
}
