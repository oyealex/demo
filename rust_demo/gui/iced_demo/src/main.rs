use iced::widget::text_input;
use iced::window::{Level, Position};
use iced::{Element, Error, Sandbox, Settings};

fn main() -> Result<(), Error> {
    Editor::run(Settings {
        id: None,
        window: iced::window::Settings {
            size: (1024, 768),
            position: Position::default(),
            min_size: None,
            max_size: None,
            visible: true,
            resizable: true,
            decorations: false,
            transparent: false,
            level: Level::default(),
            icon: None,
            platform_specific: Default::default(),
        },
        flags: Default::default(),
        default_font: Default::default(),
        default_text_size: 16.0,
        antialiasing: false,
        exit_on_close_request: true,
    })
}

struct Editor {
    content: String,
}

#[derive(Debug, Clone)]
enum Message {
    Edit(String),
}

impl Sandbox for Editor {
    type Message = Message;

    fn new() -> Self {
        Self {
            content: "value".into(),
        }
    }

    fn title(&self) -> String {
        String::from("A Cool Editor")
    }

    fn update(&mut self, message: Message) {
        match message {
            Message::Edit(value) => self.content = value,
        }
    }

    fn view(&self) -> Element<'_, Message> {
        text_input("placeholder", &self.content)
            .on_input(Message::Edit)
            .into()
    }
}
