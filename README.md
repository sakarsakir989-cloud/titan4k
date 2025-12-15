# CloudStream FilmizleTV Plugin

A CloudStream plugin for accessing FilmizleTV content.

## Overview

The FilmizleTV plugin extends CloudStream's capabilities to support streaming from FilmizleTV, a popular Turkish streaming platform. This plugin enables users to browse, search, and stream movies and TV series directly through CloudStream.

## Features

- **Browse Content**: Explore movies and TV series from FilmizleTV
- **Search Functionality**: Search for specific titles across the platform
- **Multiple Qualities**: Support for various video quality options
- **Subtitle Support**: Automatic subtitle loading and selection
- **Episode Management**: Full support for TV series with episode lists
- **Direct Streaming**: Stream directly to your device without external apps

## Installation

1. Ensure you have CloudStream installed on your device
2. Add this plugin to your CloudStream plugins folder
3. Enable the FilmizleTV plugin in CloudStream settings
4. Restart CloudStream to load the plugin

## Usage

### Basic Usage

1. Open CloudStream
2. Navigate to the plugins section
3. Select FilmizleTV from the available plugins
4. Browse or search for content
5. Select a title to view details and available episodes
6. Click play to start streaming

### Searching for Content

Use the search feature to find specific movies or TV series:
- Enter the title name in the search box
- Results will be filtered as you type
- Select from the search results to view details

### Selecting Quality and Subtitles

When playing a video:
- Click the quality/settings icon to select video quality
- Choose your preferred subtitle language or turn off subtitles
- Settings are saved for future playback

## Supported Content

- **Movies**: Full-length films with multiple quality options
- **TV Series**: Complete series with season and episode organization
- **Documentaries**: Educational and documentary content
- **Turkish Content**: Primary focus on Turkish cinema and television

## Configuration

The plugin can be customized through CloudStream settings:

```
FilmizleTV Plugin Settings:
├── Preferred Quality: 1080p, 720p, 480p, 360p
├── Default Subtitle Language: Turkish, English, etc.
├── Auto-Play: Enable/Disable automatic playback
└── Cache Settings: Adjust caching for better performance
```

## Troubleshooting

### Plugin Not Loading
- Ensure CloudStream is fully updated
- Check that the plugin file is in the correct directory
- Restart CloudStream after installation

### Streaming Issues
- Check your internet connection speed
- Try reducing the video quality
- Clear the CloudStream cache
- Update the plugin to the latest version

### No Subtitles
- Verify subtitles are enabled in settings
- Check subtitle availability for the selected content
- Try switching to a different subtitle source

## API Endpoints

The plugin utilizes the following main endpoints:

- **Home**: Fetches featured and trending content
- **Search**: Queries the FilmizleTV database
- **Details**: Retrieves full information about movies/series
- **Episodes**: Lists all episodes for a TV series
- **Stream**: Provides streaming links and quality options

## Performance

Optimized for:
- Fast content discovery
- Minimal buffering
- Low bandwidth modes
- Support for older devices

## Compatibility

- **Minimum CloudStream Version**: 5.x or higher
- **Android**: Android 5.0+
- **Device RAM**: 1GB minimum (2GB+ recommended)
- **Internet**: Stable connection (5+ Mbps for HD streaming)

## Legal Notice

This plugin is provided for educational purposes. Users are responsible for ensuring they comply with local laws and FilmizleTV's terms of service. The developers are not responsible for any misuse of this plugin.

## Contributing

Contributions are welcome! If you find bugs or have feature suggestions:

1. Open an issue with detailed information
2. Fork the repository and create a feature branch
3. Submit a pull request with your improvements
4. Ensure code follows the project's style guidelines

## Support

For issues, questions, or feature requests:
- Open an issue on the repository
- Check existing issues for similar problems
- Provide detailed information including error messages and logs

## Changelog

### Version 1.0.0
- Initial release
- Core streaming functionality
- Search and browse features
- Subtitle support
- Multiple quality options

## License

This project is provided as-is for educational purposes.

## Disclaimer

This plugin is not affiliated with, endorsed by, or connected to FilmizleTV. Use at your own risk and in compliance with applicable laws and terms of service.

---

**Last Updated**: 2025-12-15

For the latest information and updates, please check the repository regularly.
