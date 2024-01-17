use fltk::{app, enums, frame, window};
use fltk::prelude::{GroupExt, WidgetExt};

// #![windows_subsystem = "windows"]
mod examples;

fn main() {
    // let app = app::App::default().load_system_fonts();
    // // 要加载指定路径的字体的话，参见 App::load_font() 函数
    // let fonts = app::fonts();
    // // println!("{:?}", fonts);
    // let mut wind = window::Window::default().with_size(400, 300);
    // let mut frame = frame::Frame::default().size_of(&wind);
    // frame.set_label_size(30);
    // wind.set_color(enums::Color::White);
    // wind.end();
    // wind.show();
    // println!("The system has {} fonts!\nStarting slideshow!", fonts.len());
    // let mut i = 0;
    // while app.wait() {
    //     if i == fonts.len() {
    //         i = 0;
    //     }
    //     frame.set_label(&format!("[{}]", fonts[i]));
    //     frame.set_label_font(enums::Font::by_index(i));
    //     println!("index: {i}, font: {}", fonts[i]);
    //     app::sleep(0.01);
    //     i += 1;
    // }
    examples::advance_palette::run();
}
