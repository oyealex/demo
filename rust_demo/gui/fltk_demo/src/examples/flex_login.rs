#![allow(dead_code)]

use fltk::app::{App, Scheme};
use fltk::button::Button;
use fltk::enums::{Align, Color, FrameType};
use fltk::frame::Frame;
use fltk::group::Flex;
use fltk::input::Input;
use fltk::prelude::{GroupExt, WidgetBase, WidgetExt, WindowExt};
use fltk::window::Window;

pub fn run() {
    let app = App::default().with_scheme(Scheme::Gtk);

    // Flex::debug(true); // 显示布局边界

    // 置于屏幕中央
    let mut window = Window::default().with_size(600, 300).center_screen();
    window.set_color(Color::by_index(55));

    let mut main_col = Flex::default_fill().column(); // 填满父组件
    Frame::default(); // 顶部留白

    let mut main_row = Flex::default().row(); // 内容行
    {
        Frame::default(); // 左侧留白

        // 图片
        let mut image = Frame::default().with_label("Image");
        image.set_color(Color::by_index(2)); // 按索引取颜色
        image.set_frame(FrameType::BorderBox); // 边框
        main_row.fixed(&image, 240); // 宽度

        let mut input_col = Flex::default().column(); // 输入列
        {
            Frame::default(); // 上方留白

            // 欢迎标题
            let title = Frame::default().with_label("Welcome to Flex Login");
            input_col.fixed(&title, 60);

            let space = Frame::default(); // 中部留白
            input_col.fixed(&space, 10);

            let mut username_row = Flex::default().row(); // 用户名行
            {
                // 标签
                Frame::default()
                    .with_label("Username:")
                    .with_align(Align::Inside | Align::Right);

                let username_input = Input::default(); // 输入
                username_row.fixed(&username_input, 180);

                username_row.end();
            }
            input_col.fixed(&username_row, 30);

            let mut password_row = Flex::default().row(); // 密码行
            {
                // 标签
                Frame::default()
                    .with_label("Password:")
                    .with_align(Align::Inside | Align::Right);

                let password_input = Input::default(); // 输入
                password_row.fixed(&password_input, 180);

                password_row.end();
            }
            input_col.fixed(&password_row, 30);

            let button_row = Flex::default().row(); // 按钮行
            {
                Frame::default(); // 左侧留白

                // 注册
                let mut register_button = Button::default().with_label("Register");
                register_button.set_color(Color::by_index(53));

                // 登录
                let mut login_button = Button::default().with_label("Login");
                login_button.set_color(Color::by_index(53));

                Frame::default(); // 右侧留白

                button_row.end();
            }
            input_col.fixed(&button_row, 30);

            Frame::default(); // 底部留白

            input_col.end();
        }
        main_row.fixed(&input_col, 300); // 输入列宽度

        Frame::default();

        main_row.end();
    }
    main_col.fixed(&main_row, 240); // 内容行高度

    Frame::default();
    main_col.end();

    // window.resizable(&main_col); // main_col可以调整大小
    window.end();
    window.show();

    app.run().unwrap();
}
