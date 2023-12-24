use std::cell::RefCell;
use std::rc::Rc;
use fltk::app::{App, Scheme};
use fltk::{app, draw, window};
use fltk::enums::{Color, Event};
use fltk::prelude::{GroupExt, WidgetBase, WidgetExt, WindowExt};
use fltk::window::Window;

pub fn run() {
    let app = App::default().with_scheme(Scheme::Gtk);

    let mut window = Window::default()
        .with_size(800, 600)
        .center_screen()
        .with_label("Move");
    window.end();
    window.show();

    let block_pos_x = Rc::from(RefCell::from(380));
    let block_pos_y = Rc::from(RefCell::from(280));

    window.draw({
        let block_pos_x = block_pos_x.clone();
        let block_pos_y = block_pos_y.clone();
        move |_| {
            draw::set_draw_color(Color::Blue);
            let pos = *block_pos_x.borrow();
            draw::draw_rectf(pos.0, pos.1, 40, 40);
            println!("draw {pos:?}");
        }
    });

    window.handle({
        let block_pos = block_pos_x.clone();
        move |_, event| {
            match event {
                Event::Focus => true,
                Event::Move => {
                    let mut pos = *block_pos.borrow_mut();
                    pos.0 = app::event_coords().0 - 20;
                    pos.1 = app::event_coords().1 - 20;
                    println!("move {pos:?}");
                    true
                },
                _ => false,
            }
        }
    });

    app::add_idle3(move |_|{
        window.redraw();
        app::sleep(0.016);
    });

    app::run().unwrap();
}
