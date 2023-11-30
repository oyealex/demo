#![cfg_attr(not(debug_assertions), windows_subsystem = "windows")] // hide console window on Windows in release

use eframe::egui;
use egui_demo_lib::easy_mark::EasyMarkEditor;

fn main() -> Result<(), eframe::Error> {
    env_logger::init(); // Log to stderr (if you run with `RUST_LOG=debug`).
    let options = eframe::NativeOptions {
        // initial_window_size: Some(egui::vec2(320.0, 240.0)),
        ..Default::default()
    };
    eframe::run_native(
        "egui example: easy mark editor",
        options,
        Box::new(|_cc| Box::new(MyApp {
            editor: EasyMarkEditor::default(),
        })),
    )
}

struct MyApp {
    editor: EasyMarkEditor,
}

impl eframe::App for MyApp {
    fn update(&mut self, ctx: &egui::Context, _frame: &mut eframe::Frame) {
        self.editor.panels(ctx);
    }
}
