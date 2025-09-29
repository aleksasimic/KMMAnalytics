//
//  ContentView.swift
//  KMMAnalyticsDemo-iOS
//
//  Created by Aleksa Simic on 9/27/25.
//

import SwiftUI
import KMMAnalytics

struct ContentView: View {
    var body: some View {
        VStack {
            Image(systemName: "globe")
                .imageScale(.large)
                .foregroundStyle(.tint)
            Text("Hello, world! \(CustomFibiKt.generateFibi())")
        }
        .padding()
        .onAppear {
            KMMAnalyticsEventSenderKt.sendHomeScreenViewedEvent()
            AnalyticsEventsKt.home_screen_viewed(
                screen_name: "home_screen",
                timestamp: Int32(20)
            )
        }
    }
}

#Preview {
    ContentView()
}
