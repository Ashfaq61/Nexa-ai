class ToolReport {
  final String title;
  final String source; // 'github' or 'huggingface'
  final String description;
  final String url;
  final int metricCount; // stars or downloads
  final DateTime scoutedAt;

  ToolReport({
    required this.title,
    required this.source,
    required this.description,
    required this.url,
    required this.metricCount,
    required this.scoutedAt,
  });

  Map<String, dynamic> toJson() => {
    'title': title,
    'source': source,
    'description': description,
    'url': url,
    'metricCount': metricCount,
    'scoutedAt': scoutedAt.toIso8601String(),
  };

  factory ToolReport.fromJson(Map<String, dynamic> json) => ToolReport(
    title: json['title'] as String,
    source: json['source'] as String,
    description: json['description'] as String,
    url: json['url'] as String,
    metricCount: (json['metricCount'] as num?)?.toInt() ?? 0,
    scoutedAt: DateTime.parse(json['scoutedAt'] as String),
  );
}
